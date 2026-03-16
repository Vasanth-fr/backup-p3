package com.revconnect.post.service;

import com.revconnect.post.client.InteractionClient;
import com.revconnect.post.client.UserClient;
import com.revconnect.post.dto.CreatePostRequest;
import com.revconnect.post.dto.FeedResponse;
import com.revconnect.post.dto.PostResponse;
import com.revconnect.post.dto.UpdatePostRequest;
import com.revconnect.post.entity.Post;
import com.revconnect.post.repository.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("PostService Unit Tests")
class PostServiceTest {

    @Mock private PostRepository postRepository;
    @Mock private InteractionClient interactionClient;
    @Mock private UserClient userClient;

    @InjectMocks private PostService postService;

    private Post samplePost;

    @BeforeEach
    void setUp() {
        samplePost = Post.builder()
                .id(1L).userId(10L)
                .content("Hello world")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    // ── createPost ────────────────────────────────────────────────────

    @Test
    @DisplayName("createPost - saves post and returns response with correct content")
    void createPost_savesAndReturnsResponse() {
        CreatePostRequest req = CreatePostRequest.builder().content("Hello world").build();
        when(postRepository.save(any(Post.class))).thenReturn(samplePost);
        when(userClient.getUserDetails(anyLong())).thenReturn(Map.of("username", "alice", "fullName", "Alice"));

        PostResponse response = postService.createPost(10L, req);

        assertThat(response.getContent()).isEqualTo("Hello world");
        assertThat(response.getUserId()).isEqualTo(10L);
        verify(postRepository).save(any(Post.class));
    }

    // ── updatePost ────────────────────────────────────────────────────

    @Test
    @DisplayName("updatePost - updates content and returns updated response")
    void updatePost_updatesContent() {
        UpdatePostRequest req = new UpdatePostRequest();
        req.setContent("Updated content");

        when(postRepository.findById(1L)).thenReturn(Optional.of(samplePost));
        when(postRepository.save(any(Post.class))).thenAnswer(inv -> inv.getArgument(0));
        when(userClient.getUserDetails(anyLong())).thenReturn(Map.of("username", "alice"));

        PostResponse response = postService.updatePost(1L, 10L, req);

        assertThat(response.getContent()).isEqualTo("Updated content");
    }

    @Test
    @DisplayName("updatePost - different userId throws RuntimeException (unauthorized)")
    void updatePost_differentUser_throwsUnauthorized() {
        when(postRepository.findById(1L)).thenReturn(Optional.of(samplePost));

        UpdatePostRequest req = new UpdatePostRequest();
        req.setContent("Hacked");

        assertThatThrownBy(() -> postService.updatePost(1L, 99L, req))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Unauthorized");
    }

    @Test
    @DisplayName("updatePost - post not found throws RuntimeException")
    void updatePost_notFound_throwsException() {
        when(postRepository.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> postService.updatePost(99L, 10L, new UpdatePostRequest()))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Post not found");
    }

    // ── deletePost ────────────────────────────────────────────────────

    @Test
    @DisplayName("deletePost - owner can delete post")
    void deletePost_ownerDeletes_success() {
        when(postRepository.findById(1L)).thenReturn(Optional.of(samplePost));

        postService.deletePost(1L, 10L);

        verify(postRepository).delete(samplePost);
    }

    @Test
    @DisplayName("deletePost - non-owner throws RuntimeException")
    void deletePost_nonOwner_throwsException() {
        when(postRepository.findById(1L)).thenReturn(Optional.of(samplePost));

        assertThatThrownBy(() -> postService.deletePost(1L, 99L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Unauthorized");
    }

    // ── getPost ───────────────────────────────────────────────────────

    @Test
    @DisplayName("getPost - returns post with interaction counts")
    void getPost_returnsPostWithCounts() {
        when(postRepository.findById(1L)).thenReturn(Optional.of(samplePost));
        when(userClient.getUserDetails(anyLong())).thenReturn(Map.of("username", "alice"));
        when(interactionClient.getInteractionCounts(1L)).thenReturn(Map.of("likeCount", 5L, "commentCount", 3L));
        when(interactionClient.checkUserLiked(1L, 10L)).thenReturn(Map.of("hasLiked", true, "likeCount", 5L));

        PostResponse response = postService.getPost(1L, 10L);

        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getLikeCount()).isEqualTo(5L);
        assertThat(response.getCommentCount()).isEqualTo(3L);
        assertThat(response.getLikedByCurrentUser()).isTrue();
    }

    @Test
    @DisplayName("getPost - not found throws RuntimeException")
    void getPost_notFound_throwsException() {
        when(postRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> postService.getPost(999L, 10L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Post not found");
    }

    // ── getUserPosts ──────────────────────────────────────────────────

    @Test
    @DisplayName("getUserPosts - returns paginated feed for user")
    void getUserPosts_returnsPaginatedFeed() {
        Page<Post> page = new PageImpl<>(List.of(samplePost), PageRequest.of(0, 20), 1);
        when(postRepository.findByUserIdOrderByCreatedAtDesc(eq(10L), any(Pageable.class))).thenReturn(page);
        when(userClient.getUserDetails(anyLong())).thenReturn(Map.of("username", "alice"));
        when(interactionClient.getInteractionCounts(anyLong())).thenReturn(Map.of("likeCount", 0L, "commentCount", 0L));
        when(interactionClient.checkUserLiked(anyLong(), anyLong())).thenReturn(Map.of("hasLiked", false, "likeCount", 0L));

        FeedResponse response = postService.getUserPosts(10L, 99L, 0, 20);

        assertThat(response.getPosts()).hasSize(1);
        assertThat(response.getTotalElements()).isEqualTo(1);
        assertThat(response.getCurrentPage()).isEqualTo(0);
    }

    @Test
    @DisplayName("getUserPosts - falls back to zero counts when interaction-service call fails")
    void getUserPosts_interactionFailure_returnsPosts() {
        Page<Post> page = new PageImpl<>(List.of(samplePost), PageRequest.of(0, 20), 1);
        when(postRepository.findByUserIdOrderByCreatedAtDesc(eq(10L), any(Pageable.class))).thenReturn(page);
        when(userClient.getUserDetails(anyLong())).thenReturn(Map.of("username", "alice"));
        when(interactionClient.getInteractionCounts(anyLong())).thenThrow(new RuntimeException("forbidden"));
        when(interactionClient.checkUserLiked(anyLong(), anyLong())).thenReturn(Map.of("hasLiked", true, "likeCount", 1L));

        FeedResponse response = postService.getUserPosts(10L, 99L, 0, 20);

        assertThat(response.getPosts()).hasSize(1);
        assertThat(response.getPosts().get(0).getLikeCount()).isZero();
        assertThat(response.getPosts().get(0).getCommentCount()).isZero();
        assertThat(response.getPosts().get(0).getLikedByCurrentUser()).isTrue();
    }

    @Test
    @DisplayName("getUserPosts - empty result returns empty posts list")
    void getUserPosts_noPosts_returnsEmptyList() {
        Page<Post> emptyPage = new PageImpl<>(List.of(), PageRequest.of(0, 20), 0);
        when(postRepository.findByUserIdOrderByCreatedAtDesc(eq(10L), any(Pageable.class))).thenReturn(emptyPage);

        FeedResponse response = postService.getUserPosts(10L, 99L, 0, 20);

        assertThat(response.getPosts()).isEmpty();
        assertThat(response.getTotalElements()).isEqualTo(0);
    }
}

package com.revconnect.interaction.service;

import com.revconnect.interaction.client.NotificationClient;
import com.revconnect.interaction.client.PostClient;
import com.revconnect.interaction.client.UserClient;
import com.revconnect.interaction.dto.CommentRequest;
import com.revconnect.interaction.dto.CommentResponse;
import com.revconnect.interaction.entity.Comment;
import com.revconnect.interaction.repository.CommentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CommentService Unit Tests")
class CommentServiceTest {

    @Mock private CommentRepository commentRepository;
    @Mock private PostClient postClient;
    @Mock private UserClient userClient;
    @Mock private NotificationClient notificationClient;

    @InjectMocks private CommentService commentService;

    private Comment sampleComment;

    @BeforeEach
    void setUp() {
        sampleComment = Comment.builder()
                .id(1L).postId(100L).userId(10L)
                .content("Great post!")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("addComment - saves comment and returns response")
    void addComment_savesAndReturnsResponse() {
        CommentRequest req = CommentRequest.builder().postId(100L).content("Great post!").build();
        when(commentRepository.save(any(Comment.class))).thenReturn(sampleComment);
        when(postClient.getPost(100L)).thenReturn(Map.of("id", 100L, "userId", 20L));
        when(userClient.getUserDetails(10L)).thenReturn(Map.of("username", "alice", "id", 10L));

        CommentResponse response = commentService.addComment(req, 10L);

        assertThat(response.getContent()).isEqualTo("Great post!");
        assertThat(response.getPostId()).isEqualTo(100L);
        assertThat(response.getUserId()).isEqualTo(10L);
        verify(commentRepository).save(any(Comment.class));
        verify(notificationClient).sendNotification(argThat(notification ->
                notification.get("type").equals("COMMENT")
                        && notification.get("userId").equals(20L)
                        && notification.get("referenceId").equals(100L)
                        && notification.get("message").equals("alice commented on your post")));
    }

    @Test
    @DisplayName("addComment - user client failure falls back to basic user info")
    void addComment_userClientFails_usesFallback() {
        CommentRequest req = CommentRequest.builder().postId(100L).content("Nice!").build();
        when(commentRepository.save(any(Comment.class))).thenReturn(sampleComment);
        when(postClient.getPost(100L)).thenReturn(Map.of("id", 100L, "userId", 20L));
        when(userClient.getUserDetails(anyLong())).thenThrow(new RuntimeException("User service down"));

        CommentResponse response = commentService.addComment(req, 10L);

        assertThat(response).isNotNull();
        assertThat(response.getUserDetails()).containsKey("username");
    }

    @Test
    @DisplayName("addComment - does not notify when user comments on own post")
    void addComment_ownPost_doesNotSendNotification() {
        CommentRequest req = CommentRequest.builder().postId(100L).content("Nice!").build();
        when(commentRepository.save(any(Comment.class))).thenReturn(sampleComment);
        when(postClient.getPost(100L)).thenReturn(Map.of("id", 100L, "userId", 10L));
        when(userClient.getUserDetails(10L)).thenReturn(Map.of("username", "alice", "id", 10L));

        CommentResponse response = commentService.addComment(req, 10L);

        assertThat(response).isNotNull();
        verify(notificationClient, never()).sendNotification(any());
    }

    @Test
    @DisplayName("updateComment - owner updates content successfully")
    void updateComment_ownerUpdates_success() {
        when(commentRepository.findById(1L)).thenReturn(Optional.of(sampleComment));
        when(commentRepository.save(any(Comment.class))).thenAnswer(inv -> inv.getArgument(0));
        when(userClient.getUserDetails(anyLong())).thenReturn(Map.of("username", "alice"));

        CommentResponse response = commentService.updateComment(1L, "Updated content!", 10L);

        assertThat(response.getContent()).isEqualTo("Updated content!");
        verify(commentRepository).save(any(Comment.class));
    }

    @Test
    @DisplayName("updateComment - non-owner throws IllegalStateException")
    void updateComment_nonOwner_throwsException() {
        when(commentRepository.findById(1L)).thenReturn(Optional.of(sampleComment));

        assertThatThrownBy(() -> commentService.updateComment(1L, "Hacked!", 99L))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("not authorized");
    }

    @Test
    @DisplayName("updateComment - non-existent comment throws IllegalArgumentException")
    void updateComment_notFound_throwsException() {
        when(commentRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> commentService.updateComment(999L, "Text", 10L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Comment not found");
    }

    @Test
    @DisplayName("deleteComment - owner deletes comment successfully")
    void deleteComment_ownerDeletes_success() {
        when(commentRepository.findById(1L)).thenReturn(Optional.of(sampleComment));

        commentService.deleteComment(1L, 10L);

        verify(commentRepository).delete(sampleComment);
    }

    @Test
    @DisplayName("deleteComment - non-owner throws IllegalStateException")
    void deleteComment_nonOwner_throwsException() {
        when(commentRepository.findById(1L)).thenReturn(Optional.of(sampleComment));

        assertThatThrownBy(() -> commentService.deleteComment(1L, 55L))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("not authorized");
    }

    @Test
    @DisplayName("getComments - returns all comments for a post")
    void getComments_returnsListForPost() {
        when(commentRepository.findByPostId(100L)).thenReturn(List.of(sampleComment));
        when(userClient.getUserDetails(anyLong())).thenReturn(Map.of("username", "alice"));

        List<CommentResponse> result = commentService.getComments(100L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getContent()).isEqualTo("Great post!");
    }

    @Test
    @DisplayName("getCommentCount - returns count from repository")
    void getCommentCount_returnsCount() {
        when(commentRepository.countByPostId(100L)).thenReturn(5L);

        assertThat(commentService.getCommentCount(100L)).isEqualTo(5L);
    }
}

package com.revconnect.interaction.service;

import com.revconnect.interaction.client.NotificationClient;
import com.revconnect.interaction.client.PostClient;
import com.revconnect.interaction.client.UserClient;
import com.revconnect.interaction.dto.LikeRequest;
import com.revconnect.interaction.dto.LikeResponse;
import com.revconnect.interaction.entity.Like;
import com.revconnect.interaction.repository.LikeRepository;
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

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("LikeService Unit Tests")
class LikeServiceTest {

    @Mock private LikeRepository likeRepository;
    @Mock private PostClient postClient;
    @Mock private UserClient userClient;
    @Mock private NotificationClient notificationClient;

    @InjectMocks private LikeService likeService;

    private Like sampleLike;

    @BeforeEach
    void setUp() {
        sampleLike = Like.builder()
                .id(1L).postId(100L).userId(10L)
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("likePost - saves like and returns response")
    void likePost_savesLikeAndReturnsResponse() {
        LikeRequest req = new LikeRequest();
        req.setPostId(100L);
        when(likeRepository.existsByPostIdAndUserId(100L, 10L)).thenReturn(false);
        when(likeRepository.save(any(Like.class))).thenReturn(sampleLike);
        when(postClient.getPost(100L)).thenReturn(Map.of("id", 100L, "userId", 20L));
        when(userClient.getUserDetails(10L)).thenReturn(Map.of("id", 10L, "username", "alice"));

        LikeResponse response = likeService.likePost(req, 10L);

        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getPostId()).isEqualTo(100L);
        assertThat(response.getUserId()).isEqualTo(10L);
        verify(likeRepository).save(any(Like.class));
        verify(notificationClient).sendNotification(argThat(notification ->
                notification.get("type").equals("LIKE")
                        && notification.get("userId").equals(20L)
                        && notification.get("referenceId").equals(100L)
                        && notification.get("message").equals("alice liked your post")));
    }

    @Test
    @DisplayName("likePost - duplicate like throws IllegalStateException")
    void likePost_alreadyLiked_throwsException() {
        LikeRequest req = new LikeRequest();
        req.setPostId(100L);
        when(likeRepository.existsByPostIdAndUserId(100L, 10L)).thenReturn(true);

        assertThatThrownBy(() -> likeService.likePost(req, 10L))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("already liked");

        verify(likeRepository, never()).save(any());
    }

    @Test
    @DisplayName("likePost - notification failure does not break the like operation")
    void likePost_notificationFailure_stillSavesLike() {
        LikeRequest req = new LikeRequest();
        req.setPostId(100L);
        when(likeRepository.existsByPostIdAndUserId(100L, 10L)).thenReturn(false);
        when(likeRepository.save(any(Like.class))).thenReturn(sampleLike);
        when(postClient.getPost(100L)).thenReturn(Map.of("id", 100L, "userId", 20L));
        when(userClient.getUserDetails(10L)).thenReturn(Map.of("id", 10L, "username", "alice"));
        doThrow(new RuntimeException("Notification service down"))
                .when(notificationClient).sendNotification(any());

        LikeResponse response = likeService.likePost(req, 10L);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("likePost - does not notify when user likes own post")
    void likePost_ownPost_doesNotSendNotification() {
        LikeRequest req = new LikeRequest();
        req.setPostId(100L);
        when(likeRepository.existsByPostIdAndUserId(100L, 10L)).thenReturn(false);
        when(likeRepository.save(any(Like.class))).thenReturn(sampleLike);
        when(postClient.getPost(100L)).thenReturn(Map.of("id", 100L, "userId", 10L));

        LikeResponse response = likeService.likePost(req, 10L);

        assertThat(response).isNotNull();
        verify(notificationClient, never()).sendNotification(any());
        verify(userClient, never()).getUserDetails(anyLong());
    }

    @Test
    @DisplayName("unlikePost - deletes existing like")
    void unlikePost_existingLike_deletesIt() {
        when(likeRepository.existsByPostIdAndUserId(100L, 10L)).thenReturn(true);

        likeService.unlikePost(100L, 10L);

        verify(likeRepository).deleteByPostIdAndUserId(100L, 10L);
    }

    @Test
    @DisplayName("unlikePost - like not found throws IllegalStateException")
    void unlikePost_notFound_throwsException() {
        when(likeRepository.existsByPostIdAndUserId(100L, 10L)).thenReturn(false);

        assertThatThrownBy(() -> likeService.unlikePost(100L, 10L))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Like not found");
    }

    @Test
    @DisplayName("getLikes - returns all likes for a post")
    void getLikes_returnsListOfLikes() {
        when(likeRepository.findByPostId(100L)).thenReturn(List.of(sampleLike));

        List<LikeResponse> result = likeService.getLikes(100L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getPostId()).isEqualTo(100L);
    }

    @Test
    @DisplayName("getLikeCount - returns count from repository")
    void getLikeCount_returnsCount() {
        when(likeRepository.countByPostId(100L)).thenReturn(7L);

        assertThat(likeService.getLikeCount(100L)).isEqualTo(7L);
    }

    @Test
    @DisplayName("hasUserLiked - returns true when user liked")
    void hasUserLiked_returnsTrue() {
        when(likeRepository.existsByPostIdAndUserId(100L, 10L)).thenReturn(true);

        assertThat(likeService.hasUserLiked(100L, 10L)).isTrue();
    }

    @Test
    @DisplayName("hasUserLiked - returns false when user has not liked")
    void hasUserLiked_returnsFalse() {
        when(likeRepository.existsByPostIdAndUserId(100L, 99L)).thenReturn(false);

        assertThat(likeService.hasUserLiked(100L, 99L)).isFalse();
    }
}

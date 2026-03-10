package com.revconnect.interaction.service;

import com.revconnect.interaction.client.NotificationClient;
import com.revconnect.interaction.dto.LikeRequest;
import com.revconnect.interaction.dto.LikeResponse;
import com.revconnect.interaction.entity.Like;
import com.revconnect.interaction.repository.LikeRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class LikeService {

    private final LikeRepository likeRepository;
    private final NotificationClient notificationClient;

    public LikeService(LikeRepository likeRepository, NotificationClient notificationClient) {
        this.likeRepository = likeRepository;
        this.notificationClient = notificationClient;
    }

    @Transactional
    public LikeResponse likePost(LikeRequest request, Long userId) {
        // Check if already liked
        if (likeRepository.existsByPostIdAndUserId(request.getPostId(), userId)) {
            throw new IllegalStateException("Post already liked by user");
        }

        Like like = Like.builder()
                .postId(request.getPostId())
                .userId(userId)
                .build();

        Like savedLike = likeRepository.save(like);

        // Send notification asynchronously
        try {
            Map<String, Object> notification = new HashMap<>();
            notification.put("type", "LIKE");
            notification.put("postId", savedLike.getPostId());
            notification.put("userId", savedLike.getUserId());
            notification.put("message", "Your post was liked");
            notificationClient.sendNotification(notification);
        } catch (Exception e) {
            // Log but don't fail the like operation
            System.err.println("Failed to send like notification: " + e.getMessage());
        }

        return mapToResponse(savedLike);
    }

    @Transactional
    public void unlikePost(Long postId, Long userId) {
        if (!likeRepository.existsByPostIdAndUserId(postId, userId)) {
            throw new IllegalStateException("Like not found");
        }

        likeRepository.deleteByPostIdAndUserId(postId, userId);
    }

    public List<LikeResponse> getLikes(Long postId) {
        return likeRepository.findByPostId(postId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public Long getLikeCount(Long postId) {
        return likeRepository.countByPostId(postId);
    }

    public boolean hasUserLiked(Long postId, Long userId) {
        return likeRepository.existsByPostIdAndUserId(postId, userId);
    }

    private LikeResponse mapToResponse(Like like) {
        return LikeResponse.builder()
                .id(like.getId())
                .postId(like.getPostId())
                .userId(like.getUserId())
                .createdAt(like.getCreatedAt())
                .build();
    }
}

package com.revconnect.interaction.service;

import com.revconnect.interaction.client.NotificationClient;
import com.revconnect.interaction.client.PostClient;
import com.revconnect.interaction.client.UserClient;
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
    private final PostClient postClient;
    private final UserClient userClient;
    private final NotificationClient notificationClient;

    public LikeService(LikeRepository likeRepository,
                       PostClient postClient,
                       UserClient userClient,
                       NotificationClient notificationClient) {
        this.likeRepository = likeRepository;
        this.postClient = postClient;
        this.userClient = userClient;
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
            Long postOwnerId = getPostOwnerId(savedLike.getPostId());
            if (postOwnerId != null && !postOwnerId.equals(userId)) {
                Map<String, Object> notification = new HashMap<>();
                notification.put("type", "LIKE");
                notification.put("referenceId", savedLike.getPostId());
                notification.put("userId", postOwnerId);
                notification.put("message", getActorUsername(userId) + " liked your post");
                notificationClient.sendNotification(notification);
            }
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

    private Long getPostOwnerId(Long postId) {
        Map<String, Object> postDetails = postClient.getPost(postId);
        Object postOwnerId = postDetails.get("userId");
        if (postOwnerId instanceof Number number) {
            return number.longValue();
        }
        return null;
    }

    private String getActorUsername(Long userId) {
        Map<String, Object> userDetails = userClient.getUserDetails(userId);
        Object username = userDetails.get("username");
        if (username instanceof String name && !name.isBlank()) {
            return name;
        }
        return "Someone";
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

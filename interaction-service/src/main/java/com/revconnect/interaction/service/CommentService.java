package com.revconnect.interaction.service;

import com.revconnect.interaction.client.NotificationClient;
import com.revconnect.interaction.client.UserClient;
import com.revconnect.interaction.dto.CommentRequest;
import com.revconnect.interaction.dto.CommentResponse;
import com.revconnect.interaction.entity.Comment;
import com.revconnect.interaction.repository.CommentRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final UserClient userClient;
    private final NotificationClient notificationClient;

    public CommentService(CommentRepository commentRepository,
                          UserClient userClient,
                          NotificationClient notificationClient) {
        this.commentRepository = commentRepository;
        this.userClient = userClient;
        this.notificationClient = notificationClient;
    }

    @Transactional
    public CommentResponse addComment(CommentRequest request, Long userId) {
        Comment comment = Comment.builder()
                .postId(request.getPostId())
                .userId(userId)
                .content(request.getContent())
                .build();

        Comment savedComment = commentRepository.save(comment);

        // Send notification asynchronously
        try {
            Map<String, Object> notification = new HashMap<>();
            notification.put("type", "COMMENT");
            notification.put("postId", savedComment.getPostId());
            notification.put("userId", savedComment.getUserId());
            notification.put("message", "Someone commented on your post");
            notificationClient.sendNotification(notification);
        } catch (Exception e) {
            // Log but don't fail the comment operation
            System.err.println("Failed to send comment notification: " + e.getMessage());
        }

        return mapToResponse(savedComment);
    }

    @Transactional
    public CommentResponse updateComment(Long commentId, String content, Long userId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("Comment not found"));

        if (!comment.getUserId().equals(userId)) {
            throw new IllegalStateException("User not authorized to update this comment");
        }

        comment.setContent(content);
        Comment updatedComment = commentRepository.save(comment);

        return mapToResponse(updatedComment);
    }

    @Transactional
    public void deleteComment(Long commentId, Long userId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("Comment not found"));

        if (!comment.getUserId().equals(userId)) {
            throw new IllegalStateException("User not authorized to delete this comment");
        }

        commentRepository.delete(comment);
    }

    public List<CommentResponse> getComments(Long postId) {
        return commentRepository.findByPostId(postId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public Long getCommentCount(Long postId) {
        return commentRepository.countByPostId(postId);
    }

    private CommentResponse mapToResponse(Comment comment) {
        Map<String, Object> userDetails = null;
        try {
            userDetails = userClient.getUserDetails(comment.getUserId());
        } catch (Exception e) {
            // Fallback to basic user info
            userDetails = new HashMap<>();
            userDetails.put("id", comment.getUserId());
            userDetails.put("username", "Unknown User");
        }

        return CommentResponse.builder()
                .id(comment.getId())
                .postId(comment.getPostId())
                .userId(comment.getUserId())
                .content(comment.getContent())
                .createdAt(comment.getCreatedAt())
                .userDetails(userDetails)
                .build();
    }
}

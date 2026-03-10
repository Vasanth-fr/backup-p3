package com.revconnect.interaction.dto;

import java.time.LocalDateTime;
import java.util.Map;

public class CommentResponse {

    private Long id;
    private Long postId;
    private Long userId;
    private String content;
    private LocalDateTime createdAt;
    private Map<String, Object> userDetails;

    public CommentResponse() {
    }

    private CommentResponse(Builder builder) {
        this.id = builder.id;
        this.postId = builder.postId;
        this.userId = builder.userId;
        this.content = builder.content;
        this.createdAt = builder.createdAt;
        this.userDetails = builder.userDetails;
    }

    // Getters
    public Long getId() {
        return id;
    }

    public Long getPostId() {
        return postId;
    }

    public Long getUserId() {
        return userId;
    }

    public String getContent() {
        return content;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public Map<String, Object> getUserDetails() {
        return userDetails;
    }

    // Setters
    public void setId(Long id) {
        this.id = id;
    }

    public void setPostId(Long postId) {
        this.postId = postId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setUserDetails(Map<String, Object> userDetails) {
        this.userDetails = userDetails;
    }

    // Builder
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private Long postId;
        private Long userId;
        private String content;
        private LocalDateTime createdAt;
        private Map<String, Object> userDetails;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder postId(Long postId) {
            this.postId = postId;
            return this;
        }

        public Builder userId(Long userId) {
            this.userId = userId;
            return this;
        }

        public Builder content(String content) {
            this.content = content;
            return this;
        }

        public Builder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Builder userDetails(Map<String, Object> userDetails) {
            this.userDetails = userDetails;
            return this;
        }

        public CommentResponse build() {
            return new CommentResponse(this);
        }
    }
}

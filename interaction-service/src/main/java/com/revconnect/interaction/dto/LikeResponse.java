package com.revconnect.interaction.dto;

import java.time.LocalDateTime;

public class LikeResponse {

    private Long id;
    private Long postId;
    private Long userId;
    private LocalDateTime createdAt;

    public LikeResponse() {
    }

    private LikeResponse(Builder builder) {
        this.id = builder.id;
        this.postId = builder.postId;
        this.userId = builder.userId;
        this.createdAt = builder.createdAt;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
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

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    // Builder
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private Long postId;
        private Long userId;
        private LocalDateTime createdAt;

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

        public Builder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public LikeResponse build() {
            return new LikeResponse(this);
        }
    }
}

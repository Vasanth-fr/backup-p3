package com.revconnect.interaction.dto;

import jakarta.validation.constraints.NotNull;

public class LikeRequest {

    @NotNull(message = "Post ID is required")
    private Long postId;

    public LikeRequest() {
    }

    private LikeRequest(Builder builder) {
        this.postId = builder.postId;
    }

    // Getter
    public Long getPostId() {
        return postId;
    }

    // Setter
    public void setPostId(Long postId) {
        this.postId = postId;
    }

    // Builder
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long postId;

        public Builder postId(Long postId) {
            this.postId = postId;
            return this;
        }

        public LikeRequest build() {
            return new LikeRequest(this);
        }
    }
}

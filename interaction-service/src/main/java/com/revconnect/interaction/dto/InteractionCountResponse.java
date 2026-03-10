package com.revconnect.interaction.dto;

public class InteractionCountResponse {

    private Long postId;
    private Long likeCount;
    private Long commentCount;

    public InteractionCountResponse() {
    }

    private InteractionCountResponse(Builder builder) {
        this.postId = builder.postId;
        this.likeCount = builder.likeCount;
        this.commentCount = builder.commentCount;
    }

    // Getters
    public Long getPostId() {
        return postId;
    }

    public Long getLikeCount() {
        return likeCount;
    }

    public Long getCommentCount() {
        return commentCount;
    }

    // Setters
    public void setPostId(Long postId) {
        this.postId = postId;
    }

    public void setLikeCount(Long likeCount) {
        this.likeCount = likeCount;
    }

    public void setCommentCount(Long commentCount) {
        this.commentCount = commentCount;
    }

    // Builder
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long postId;
        private Long likeCount;
        private Long commentCount;

        public Builder postId(Long postId) {
            this.postId = postId;
            return this;
        }

        public Builder likeCount(Long likeCount) {
            this.likeCount = likeCount;
            return this;
        }

        public Builder commentCount(Long commentCount) {
            this.commentCount = commentCount;
            return this;
        }

        public InteractionCountResponse build() {
            return new InteractionCountResponse(this);
        }
    }
}

package com.revconnect.interaction.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class CommentRequest {

    @NotNull(message = "Post ID is required")
    private Long postId;

    @NotBlank(message = "Content is required")
    @Size(min = 1, max = 1000, message = "Content must be between 1 and 1000 characters")
    private String content;

    public CommentRequest() {
    }

    private CommentRequest(Builder builder) {
        this.postId = builder.postId;
        this.content = builder.content;
    }

    // Getters
    public Long getPostId() {
        return postId;
    }

    public String getContent() {
        return content;
    }

    // Setters
    public void setPostId(Long postId) {
        this.postId = postId;
    }

    public void setContent(String content) {
        this.content = content;
    }

    // Builder
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long postId;
        private String content;

        public Builder postId(Long postId) {
            this.postId = postId;
            return this;
        }

        public Builder content(String content) {
            this.content = content;
            return this;
        }

        public CommentRequest build() {
            return new CommentRequest(this);
        }
    }
}

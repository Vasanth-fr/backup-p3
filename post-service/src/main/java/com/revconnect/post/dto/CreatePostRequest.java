package com.revconnect.post.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CreatePostRequest {

    @NotBlank(message = "Content cannot be empty")
    @Size(max = 5000, message = "Content must not exceed 5000 characters")
    private String content;

    @Size(max = 500, message = "Image URL must not exceed 500 characters")
    private String imageUrl;

    public CreatePostRequest() {
    }

    private CreatePostRequest(Builder builder) {
        this.content = builder.content;
        this.imageUrl = builder.imageUrl;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String content;
        private String imageUrl;

        public Builder content(String content) {
            this.content = content;
            return this;
        }

        public Builder imageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
            return this;
        }

        public CreatePostRequest build() {
            return new CreatePostRequest(this);
        }
    }
}

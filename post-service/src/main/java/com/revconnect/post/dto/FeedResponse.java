package com.revconnect.post.dto;

import java.util.List;

public class FeedResponse {

    private List<PostResponse> posts;
    private int currentPage;
    private int totalPages;
    private long totalElements;
    private int pageSize;

    public FeedResponse() {
    }

    private FeedResponse(Builder builder) {
        this.posts = builder.posts;
        this.currentPage = builder.currentPage;
        this.totalPages = builder.totalPages;
        this.totalElements = builder.totalElements;
        this.pageSize = builder.pageSize;
    }

    public List<PostResponse> getPosts() {
        return posts;
    }

    public void setPosts(List<PostResponse> posts) {
        this.posts = posts;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public long getTotalElements() {
        return totalElements;
    }

    public void setTotalElements(long totalElements) {
        this.totalElements = totalElements;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private List<PostResponse> posts;
        private int currentPage;
        private int totalPages;
        private long totalElements;
        private int pageSize;

        public Builder posts(List<PostResponse> posts) {
            this.posts = posts;
            return this;
        }

        public Builder currentPage(int currentPage) {
            this.currentPage = currentPage;
            return this;
        }

        public Builder totalPages(int totalPages) {
            this.totalPages = totalPages;
            return this;
        }

        public Builder totalElements(long totalElements) {
            this.totalElements = totalElements;
            return this;
        }

        public Builder pageSize(int pageSize) {
            this.pageSize = pageSize;
            return this;
        }

        public FeedResponse build() {
            return new FeedResponse(this);
        }
    }
}

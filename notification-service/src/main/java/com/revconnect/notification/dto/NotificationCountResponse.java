package com.revconnect.notification.dto;

public class NotificationCountResponse {

    private Long total;
    private Long unread;

    public NotificationCountResponse() {
    }

    private NotificationCountResponse(Builder builder) {
        this.total = builder.total;
        this.unread = builder.unread;
    }

    public static Builder builder() {
        return new Builder();
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public Long getUnread() {
        return unread;
    }

    public void setUnread(Long unread) {
        this.unread = unread;
    }

    public static class Builder {
        private Long total;
        private Long unread;

        public Builder total(Long total) {
            this.total = total;
            return this;
        }

        public Builder unread(Long unread) {
            this.unread = unread;
            return this;
        }

        public NotificationCountResponse build() {
            return new NotificationCountResponse(this);
        }
    }
}

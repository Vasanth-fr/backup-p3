package com.revconnect.notification.dto;

import com.revconnect.notification.entity.NotificationType;
import jakarta.validation.constraints.NotNull;

public class CreateNotificationRequest {

    @NotNull(message = "User ID is required")
    private Long userId;

    @NotNull(message = "Notification type is required")
    private NotificationType type;

    @NotNull(message = "Message is required")
    private String message;

    private Long referenceId;

    public CreateNotificationRequest() {
    }

    private CreateNotificationRequest(Builder builder) {
        this.userId = builder.userId;
        this.type = builder.type;
        this.message = builder.message;
        this.referenceId = builder.referenceId;
    }

    public static Builder builder() {
        return new Builder();
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public NotificationType getType() {
        return type;
    }

    public void setType(NotificationType type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(Long referenceId) {
        this.referenceId = referenceId;
    }

    public static class Builder {
        private Long userId;
        private NotificationType type;
        private String message;
        private Long referenceId;

        public Builder userId(Long userId) {
            this.userId = userId;
            return this;
        }

        public Builder type(NotificationType type) {
            this.type = type;
            return this;
        }

        public Builder message(String message) {
            this.message = message;
            return this;
        }

        public Builder referenceId(Long referenceId) {
            this.referenceId = referenceId;
            return this;
        }

        public CreateNotificationRequest build() {
            return new CreateNotificationRequest(this);
        }
    }
}

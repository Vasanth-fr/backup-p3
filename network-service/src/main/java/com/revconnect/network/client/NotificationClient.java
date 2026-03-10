package com.revconnect.network.client;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@FeignClient(name = "notification-service")
public interface NotificationClient {

    @PostMapping("/api/notifications/send")
    @CircuitBreaker(name = "notificationService", fallbackMethod = "sendNotificationFallback")
    void sendNotification(@RequestBody NotificationRequest request);

    default void sendNotificationFallback(NotificationRequest request, Exception e) {
        // Log the failure but don't throw exception - notifications are non-critical
        System.err.println("Failed to send notification: " + e.getMessage());
    }

    class NotificationRequest {
        private Long userId;
        private String type;
        private String message;
        private Map<String, Object> data;

        public NotificationRequest() {
        }

        private NotificationRequest(Builder builder) {
            this.userId = builder.userId;
            this.type = builder.type;
            this.message = builder.message;
            this.data = builder.data;
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

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public Map<String, Object> getData() {
            return data;
        }

        public void setData(Map<String, Object> data) {
            this.data = data;
        }

        public static class Builder {
            private Long userId;
            private String type;
            private String message;
            private Map<String, Object> data;

            public Builder userId(Long userId) {
                this.userId = userId;
                return this;
            }

            public Builder type(String type) {
                this.type = type;
                return this;
            }

            public Builder message(String message) {
                this.message = message;
                return this;
            }

            public Builder data(Map<String, Object> data) {
                this.data = data;
                return this;
            }

            public NotificationRequest build() {
                return new NotificationRequest(this);
            }
        }
    }
}

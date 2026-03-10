package com.revconnect.network.dto;

import com.revconnect.network.entity.Connection.ConnectionStatus;
import java.time.LocalDateTime;

public class ConnectionResponse {

    private Long id;
    private Long userId;
    private Long connectedUserId;
    private ConnectionStatus status;
    private LocalDateTime createdAt;
    private UserDetails userDetails;

    public ConnectionResponse() {
    }

    private ConnectionResponse(Builder builder) {
        this.id = builder.id;
        this.userId = builder.userId;
        this.connectedUserId = builder.connectedUserId;
        this.status = builder.status;
        this.createdAt = builder.createdAt;
        this.userDetails = builder.userDetails;
    }

    public static Builder builder() {
        return new Builder();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getConnectedUserId() {
        return connectedUserId;
    }

    public void setConnectedUserId(Long connectedUserId) {
        this.connectedUserId = connectedUserId;
    }

    public ConnectionStatus getStatus() {
        return status;
    }

    public void setStatus(ConnectionStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public UserDetails getUserDetails() {
        return userDetails;
    }

    public void setUserDetails(UserDetails userDetails) {
        this.userDetails = userDetails;
    }

    public static class Builder {
        private Long id;
        private Long userId;
        private Long connectedUserId;
        private ConnectionStatus status;
        private LocalDateTime createdAt;
        private UserDetails userDetails;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder userId(Long userId) {
            this.userId = userId;
            return this;
        }

        public Builder connectedUserId(Long connectedUserId) {
            this.connectedUserId = connectedUserId;
            return this;
        }

        public Builder status(ConnectionStatus status) {
            this.status = status;
            return this;
        }

        public Builder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Builder userDetails(UserDetails userDetails) {
            this.userDetails = userDetails;
            return this;
        }

        public ConnectionResponse build() {
            return new ConnectionResponse(this);
        }
    }

    public static class UserDetails {
        private Long id;
        private String username;
        private String fullName;
        private String profileImageUrl;

        public UserDetails() {
        }

        private UserDetails(Builder builder) {
            this.id = builder.id;
            this.username = builder.username;
            this.fullName = builder.fullName;
            this.profileImageUrl = builder.profileImageUrl;
        }

        public static Builder builder() {
            return new Builder();
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getFullName() {
            return fullName;
        }

        public void setFullName(String fullName) {
            this.fullName = fullName;
        }

        public String getProfileImageUrl() {
            return profileImageUrl;
        }

        public void setProfileImageUrl(String profileImageUrl) {
            this.profileImageUrl = profileImageUrl;
        }

        public static class Builder {
            private Long id;
            private String username;
            private String fullName;
            private String profileImageUrl;

            public Builder id(Long id) {
                this.id = id;
                return this;
            }

            public Builder username(String username) {
                this.username = username;
                return this;
            }

            public Builder fullName(String fullName) {
                this.fullName = fullName;
                return this;
            }

            public Builder profileImageUrl(String profileImageUrl) {
                this.profileImageUrl = profileImageUrl;
                return this;
            }

            public UserDetails build() {
                return new UserDetails(this);
            }
        }
    }
}

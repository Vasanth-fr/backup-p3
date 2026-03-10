package com.revconnect.network.dto;

import jakarta.validation.constraints.NotNull;

public class ConnectionRequest {

    @NotNull(message = "Connected user ID is required")
    private Long connectedUserId;

    public ConnectionRequest() {
    }

    private ConnectionRequest(Builder builder) {
        this.connectedUserId = builder.connectedUserId;
    }

    public static Builder builder() {
        return new Builder();
    }

    public Long getConnectedUserId() {
        return connectedUserId;
    }

    public void setConnectedUserId(Long connectedUserId) {
        this.connectedUserId = connectedUserId;
    }

    public static class Builder {
        private Long connectedUserId;

        public Builder connectedUserId(Long connectedUserId) {
            this.connectedUserId = connectedUserId;
            return this;
        }

        public ConnectionRequest build() {
            return new ConnectionRequest(this);
        }
    }
}

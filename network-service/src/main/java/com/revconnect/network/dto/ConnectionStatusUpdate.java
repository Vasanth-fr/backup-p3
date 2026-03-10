package com.revconnect.network.dto;

import com.revconnect.network.entity.Connection.ConnectionStatus;
import jakarta.validation.constraints.NotNull;

public class ConnectionStatusUpdate {

    @NotNull(message = "Status is required")
    private ConnectionStatus status;

    public ConnectionStatusUpdate() {
    }

    private ConnectionStatusUpdate(Builder builder) {
        this.status = builder.status;
    }

    public static Builder builder() {
        return new Builder();
    }

    public ConnectionStatus getStatus() {
        return status;
    }

    public void setStatus(ConnectionStatus status) {
        this.status = status;
    }

    public static class Builder {
        private ConnectionStatus status;

        public Builder status(ConnectionStatus status) {
            this.status = status;
            return this;
        }

        public ConnectionStatusUpdate build() {
            return new ConnectionStatusUpdate(this);
        }
    }
}

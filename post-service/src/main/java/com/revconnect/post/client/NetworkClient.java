package com.revconnect.post.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;

@FeignClient(name = "network-service")
public interface NetworkClient {

    @GetMapping("/api/network/connections")
    List<ConnectionResponse> getConnections(@RequestHeader("X-User-Id") Long userId);

    class ConnectionResponse {
        private Long id;
        private Long userId;
        private Long connectedUserId;
        private String status;

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

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }
    }
}

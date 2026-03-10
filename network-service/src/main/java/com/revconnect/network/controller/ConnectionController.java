package com.revconnect.network.controller;

import com.revconnect.network.dto.ConnectionRequest;
import com.revconnect.network.dto.ConnectionResponse;
import com.revconnect.network.service.ConnectionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/network")
public class ConnectionController {

    private final ConnectionService connectionService;

    public ConnectionController(ConnectionService connectionService) {
        this.connectionService = connectionService;
    }

    /**
     * Send a connection request
     * POST /api/network/connect
     */
    @PostMapping("/connect")
    public ResponseEntity<ConnectionResponse> sendConnectionRequest(
            @RequestHeader("X-User-Id") Long userId,
            @Valid @RequestBody ConnectionRequest request) {
        ConnectionResponse response = connectionService.sendRequest(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Accept a connection request
     * PUT /api/network/connections/{connectionId}/accept
     */
    @PutMapping("/connections/{connectionId}/accept")
    public ResponseEntity<ConnectionResponse> acceptConnectionRequest(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long connectionId) {
        ConnectionResponse response = connectionService.acceptRequest(userId, connectionId);
        return ResponseEntity.ok(response);
    }

    /**
     * Reject a connection request
     * PUT /api/network/connections/{connectionId}/reject
     */
    @PutMapping("/connections/{connectionId}/reject")
    public ResponseEntity<ConnectionResponse> rejectConnectionRequest(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long connectionId) {
        ConnectionResponse response = connectionService.rejectRequest(userId, connectionId);
        return ResponseEntity.ok(response);
    }

    /**
     * Delete/Remove a connection
     * DELETE /api/network/connections/{connectionId}
     */
    @DeleteMapping("/connections/{connectionId}")
    public ResponseEntity<Void> deleteConnection(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long connectionId) {
        connectionService.deleteConnection(userId, connectionId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Get all accepted connections for a user
     * GET /api/network/connections
     */
    @GetMapping("/connections")
    public ResponseEntity<List<ConnectionResponse>> getConnections(
            @RequestHeader("X-User-Id") Long userId) {
        List<ConnectionResponse> connections = connectionService.getConnections(userId);
        return ResponseEntity.ok(connections);
    }

    /**
     * Get all pending connection requests for a user
     * GET /api/network/pending
     */
    @GetMapping("/pending")
    public ResponseEntity<List<ConnectionResponse>> getPendingRequests(
            @RequestHeader("X-User-Id") Long userId) {
        List<ConnectionResponse> pendingRequests = connectionService.getPendingRequests(userId);
        return ResponseEntity.ok(pendingRequests);
    }

    /**
     * Check connection status between current user and another user
     * GET /api/network/check/{userId}
     */
    @GetMapping("/check/{otherUserId}")
    public ResponseEntity<Map<String, Object>> checkConnection(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long otherUserId) {
        Map<String, Object> connectionStatus = connectionService.checkConnection(userId, otherUserId);
        return ResponseEntity.ok(connectionStatus);
    }

    /**
     * Get connection count for a user
     * GET /api/network/count
     */
    @GetMapping("/count")
    public ResponseEntity<Map<String, Long>> getConnectionCount(
            @RequestHeader("X-User-Id") Long userId) {
        Long count = connectionService.getConnectionCount(userId);
        return ResponseEntity.ok(Map.of("count", count));
    }

    /**
     * Exception handler for IllegalArgumentException
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgument(IllegalArgumentException e) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", e.getMessage()));
    }

    /**
     * Exception handler for IllegalStateException
     */
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Map<String, String>> handleIllegalState(IllegalStateException e) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(Map.of("error", e.getMessage()));
    }

    /**
     * General exception handler
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleException(Exception e) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "An unexpected error occurred: " + e.getMessage()));
    }
}

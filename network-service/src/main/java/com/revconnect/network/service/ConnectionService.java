package com.revconnect.network.service;

import com.revconnect.network.client.NotificationClient;
import com.revconnect.network.client.NotificationClient.NotificationRequest;
import com.revconnect.network.client.UserClient;
import com.revconnect.network.dto.ConnectionRequest;
import com.revconnect.network.dto.ConnectionResponse;
import com.revconnect.network.dto.ConnectionResponse.UserDetails;
import com.revconnect.network.entity.Connection;
import com.revconnect.network.entity.Connection.ConnectionStatus;
import com.revconnect.network.repository.ConnectionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ConnectionService {

    private final ConnectionRepository connectionRepository;
    private final UserClient userClient;
    private final NotificationClient notificationClient;

    public ConnectionService(ConnectionRepository connectionRepository,
                           UserClient userClient,
                           NotificationClient notificationClient) {
        this.connectionRepository = connectionRepository;
        this.userClient = userClient;
        this.notificationClient = notificationClient;
    }

    @Transactional
    public ConnectionResponse sendRequest(Long userId, ConnectionRequest request) {
        // Prevent self-connection
        if (userId.equals(request.getConnectedUserId())) {
            throw new IllegalArgumentException("Cannot send connection request to yourself");
        }

        // Check if connection already exists
        var existingConnection = connectionRepository.findConnectionBetweenUsers(userId, request.getConnectedUserId());
        if (existingConnection.isPresent()) {
            throw new IllegalStateException("Connection already exists between these users");
        }

        // Create new connection request
        Connection connection = Connection.builder()
                .userId(userId)
                .connectedUserId(request.getConnectedUserId())
                .status(ConnectionStatus.PENDING)
                .build();

        connection = connectionRepository.save(connection);

        // Send notification to the receiving user
        sendConnectionNotification(
            request.getConnectedUserId(),
            "CONNECTION_REQUEST",
            "You have a new connection request",
            userId,
            connection.getId()
        );

        return mapToResponse(connection);
    }

    @Transactional
    public ConnectionResponse acceptRequest(Long userId, Long connectionId) {
        Connection connection = connectionRepository.findById(connectionId)
                .orElseThrow(() -> new IllegalArgumentException("Connection not found"));

        // Verify the user is the recipient of the connection request
        if (!connection.getConnectedUserId().equals(userId)) {
            throw new IllegalArgumentException("You are not authorized to accept this connection");
        }

        if (connection.getStatus() != ConnectionStatus.PENDING) {
            throw new IllegalStateException("Connection request is not in pending state");
        }

        connection.setStatus(ConnectionStatus.ACCEPTED);
        connection = connectionRepository.save(connection);

        // Send notification to the user who sent the request
        sendConnectionNotification(
            connection.getUserId(),
            "CONNECTION_ACCEPTED",
            "Your connection request was accepted",
            userId,
            connection.getId()
        );

        return mapToResponse(connection);
    }

    @Transactional
    public ConnectionResponse rejectRequest(Long userId, Long connectionId) {
        Connection connection = connectionRepository.findById(connectionId)
                .orElseThrow(() -> new IllegalArgumentException("Connection not found"));

        // Verify the user is the recipient of the connection request
        if (!connection.getConnectedUserId().equals(userId)) {
            throw new IllegalArgumentException("You are not authorized to reject this connection");
        }

        if (connection.getStatus() != ConnectionStatus.PENDING) {
            throw new IllegalStateException("Connection request is not in pending state");
        }

        connection.setStatus(ConnectionStatus.REJECTED);
        connection = connectionRepository.save(connection);

        return mapToResponse(connection);
    }

    @Transactional
    public void deleteConnection(Long userId, Long connectionId) {
        Connection connection = connectionRepository.findById(connectionId)
                .orElseThrow(() -> new IllegalArgumentException("Connection not found"));

        // Verify the user is part of this connection
        if (!connection.getUserId().equals(userId) && !connection.getConnectedUserId().equals(userId)) {
            throw new IllegalArgumentException("You are not authorized to delete this connection");
        }

        connectionRepository.delete(connection);

        // Notify the other user about the connection removal
        Long otherUserId = connection.getUserId().equals(userId)
            ? connection.getConnectedUserId()
            : connection.getUserId();

        sendConnectionNotification(
            otherUserId,
            "CONNECTION_REMOVED",
            "A user has removed their connection with you",
            userId,
            connection.getId()
        );
    }

    public List<ConnectionResponse> getConnections(Long userId) {
        // Get all accepted connections where user is either initiator or receiver
        List<Connection> connections = connectionRepository.findConnectionsBetweenUsers(userId, ConnectionStatus.ACCEPTED);
        return connections.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<ConnectionResponse> getPendingRequests(Long userId) {
        // Get all pending requests received by the user
        List<Connection> pendingRequests = connectionRepository.findPendingRequestsForUser(userId);
        return pendingRequests.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public Map<String, Object> checkConnection(Long userId, Long otherUserId) {
        var connection = connectionRepository.findConnectionBetweenUsers(userId, otherUserId);

        Map<String, Object> result = new HashMap<>();
        result.put("connected", connection.isPresent() && connection.get().getStatus() == ConnectionStatus.ACCEPTED);
        result.put("status", connection.map(c -> c.getStatus().name()).orElse("NONE"));
        result.put("connectionId", connection.map(Connection::getId).orElse(null));

        return result;
    }

    public Long getConnectionCount(Long userId) {
        return connectionRepository.countAcceptedConnections(userId);
    }

    private ConnectionResponse mapToResponse(Connection connection) {
        // Fetch user details for the connected user
        UserDetails userDetails = null;
        try {
            Long detailsUserId = connection.getConnectedUserId();
            userDetails = userClient.getUserDetails(detailsUserId);
        } catch (Exception e) {
            // Fallback will be handled by circuit breaker
            userDetails = new UserDetails();
            userDetails.setId(connection.getConnectedUserId());
            userDetails.setUsername("Unknown User");
        }

        return ConnectionResponse.builder()
                .id(connection.getId())
                .userId(connection.getUserId())
                .connectedUserId(connection.getConnectedUserId())
                .status(connection.getStatus())
                .createdAt(connection.getCreatedAt())
                .userDetails(userDetails)
                .build();
    }

    private void sendConnectionNotification(Long userId, String type, String message, Long fromUserId, Long connectionId) {
        try {
            Map<String, Object> data = new HashMap<>();
            data.put("fromUserId", fromUserId);
            data.put("connectionId", connectionId);

            NotificationRequest notification = NotificationRequest.builder()
                    .userId(userId)
                    .type(type)
                    .message(message)
                    .data(data)
                    .build();

            notificationClient.sendNotification(notification);
        } catch (Exception e) {
            // Notification failures should not break the main flow
            System.err.println("Failed to send notification: " + e.getMessage());
        }
    }
}

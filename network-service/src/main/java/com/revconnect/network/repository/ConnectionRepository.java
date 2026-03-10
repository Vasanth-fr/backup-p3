package com.revconnect.network.repository;

import com.revconnect.network.entity.Connection;
import com.revconnect.network.entity.Connection.ConnectionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConnectionRepository extends JpaRepository<Connection, Long> {

    /**
     * Find connection between two specific users
     */
    Optional<Connection> findByUserIdAndConnectedUserId(Long userId, Long connectedUserId);

    /**
     * Find all connections for a user with a specific status
     */
    List<Connection> findByUserIdAndStatus(Long userId, ConnectionStatus status);

    /**
     * Find all connections for a user (regardless of status)
     */
    List<Connection> findByUserId(Long userId);

    /**
     * Find connections where the user is either the initiator or the receiver
     */
    @Query("SELECT c FROM Connection c WHERE (c.userId = :userId OR c.connectedUserId = :userId) AND c.status = :status")
    List<Connection> findConnectionsBetweenUsers(@Param("userId") Long userId, @Param("status") ConnectionStatus status);

    /**
     * Find all pending connection requests received by a user
     */
    @Query("SELECT c FROM Connection c WHERE c.connectedUserId = :userId AND c.status = 'PENDING'")
    List<Connection> findPendingRequestsForUser(@Param("userId") Long userId);

    /**
     * Find all pending connection requests sent by a user
     */
    @Query("SELECT c FROM Connection c WHERE c.userId = :userId AND c.status = 'PENDING'")
    List<Connection> findPendingRequestsByUser(@Param("userId") Long userId);

    /**
     * Count connections by user ID and status
     */
    Long countByUserIdAndStatus(Long userId, ConnectionStatus status);

    /**
     * Count all accepted connections for a user (both directions)
     */
    @Query("SELECT COUNT(c) FROM Connection c WHERE (c.userId = :userId OR c.connectedUserId = :userId) AND c.status = 'ACCEPTED'")
    Long countAcceptedConnections(@Param("userId") Long userId);

    /**
     * Check if connection exists between two users (in either direction)
     */
    @Query("SELECT c FROM Connection c WHERE ((c.userId = :userId1 AND c.connectedUserId = :userId2) OR (c.userId = :userId2 AND c.connectedUserId = :userId1))")
    Optional<Connection> findConnectionBetweenUsers(@Param("userId1") Long userId1, @Param("userId2") Long userId2);
}

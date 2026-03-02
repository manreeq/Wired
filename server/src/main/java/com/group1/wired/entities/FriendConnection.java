package com.group1.wired.entities;

import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;
import jakarta.persistence.*;

@Entity
@Table(name = "friend_connections")
public class FriendConnection {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long connectionId;

    // fk 1: user who sent the request
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requester_user_id", nullable = false)
    private User requesterUser;

    // fk 2: user receiving the request
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_user_id", nullable = false)
    private User targetUser;

    // sets default status to pending
    @Column(nullable = false)
    private String status = "Pending";

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    protected FriendConnection() {}

    public FriendConnection(User requesterUser, User targetUser) {
        this.requesterUser = requesterUser;
        this.targetUser = targetUser;
    }

    public Long getConnectionId() {
        return connectionId;
    }

    protected void setConnectionId(Long connectionId) {
        this.connectionId = connectionId;
    }

    public User getRequesterUser() {
        return requesterUser;
    }

    public void setRequesterUser(User requesterUser) {
        this.requesterUser = requesterUser;
    }

    public User getTargetUser() {
        return targetUser;
    }

    public void setTargetUser(User targetUser) {
        this.targetUser = targetUser;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
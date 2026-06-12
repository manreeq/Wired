package com.group1.wired.service;

import com.group1.wired.entities.FriendConnection;
import com.group1.wired.entities.User;
import com.group1.wired.repositories.FriendConnectionRepository;
import com.group1.wired.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FriendService {

    private final FriendConnectionRepository connectionRepo;
    private final UserRepository userRepo;

    public FriendService(FriendConnectionRepository connectionRepo, UserRepository userRepo) {
        this.connectionRepo = connectionRepo;
        this.userRepo = userRepo;
    }

    // Create a new friend connection
    public FriendConnection createFriendConnection(Long requesterId, String targetFriendCode, String status) {
        User requester = userRepo.findById(requesterId)
                .orElseThrow(() -> new IllegalArgumentException("Requester not found"));
        User target = userRepo.findByFriendCode(targetFriendCode)
                .orElseThrow(() -> new IllegalArgumentException("Target not found"));
        // Shouldn't be able to add yourself
        if (requester.getUserID().equals(target.getUserID())) {
            throw new IllegalArgumentException("You cannot send a friend request to yourself");
        }
        // Prevents requests when there's pending or accepted entries between the target and requester
        boolean existsActiveConnection =
                connectionRepo.findByRequesterUserAndTargetUser(requester, target)
                        .map(conn -> "Pending".equals(conn.getStatus()) || "Accepted".equals(conn.getStatus()))
                        .orElse(false)
                        ||
                connectionRepo.findByRequesterUserAndTargetUser(target, requester)
                        .map(conn -> "Pending".equals(conn.getStatus()) || "Accepted".equals(conn.getStatus()))
                        .orElse(false);

        if (existsActiveConnection) {
            throw new IllegalStateException("Friend request already exists or you are already friends");
        }

        FriendConnection connection = new FriendConnection(requester, target);
        connection.setStatus(status != null ? status : "Pending");
        return connectionRepo.save(connection);
    }

    // Get friend list
    public List<FriendConnection> getFriendList(Long userId) {
        User localUser = userRepo.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));
        return connectionRepo.findByRequesterUserOrTargetUser(localUser, localUser);
    }

    // Get all pending requests for a user
    public List<FriendConnection> getPendingRequests(Long userId) {
        User targetUser = userRepo.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));
        return connectionRepo.findByTargetUserAndStatus(targetUser, "Pending");
    }

    // Get all requests a user has sent
    public List<FriendConnection> getSentRequests(User requesterUser) {
        return connectionRepo.findByRequesterUserAndStatus(requesterUser, "Pending");
    }

    // Check if two users are already connected
    public Optional<FriendConnection> findConnection(User requesterUser, User targetUser) {
        return connectionRepo.findByRequesterUserAndTargetUser(requesterUser, targetUser);
    }

    // change friend connection status to accepted
    public FriendConnection acceptFriendRequest(Long connectionId) {
        FriendConnection connection = connectionRepo.findById(connectionId)
                .orElseThrow(() -> new IllegalArgumentException("Connection not found"));
        connection.setStatus("Accepted");
        return connectionRepo.save(connection);
    }
    // change friend connection status to declined
    public FriendConnection declineFriendRequest(Long connectionId) {
        FriendConnection connection = connectionRepo.findById(connectionId)
                .orElseThrow(() -> new IllegalArgumentException("Connection not found"));
        connection.setStatus("Declined");
        return connectionRepo.save(connection);
    }

    public void removeFriend(Long connectionId) {
        FriendConnection connection = connectionRepo.findById(connectionId)
                .orElseThrow(() -> new IllegalArgumentException("Connection not found"));
        connectionRepo.delete(connection);
    }

}

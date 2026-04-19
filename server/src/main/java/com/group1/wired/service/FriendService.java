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

}

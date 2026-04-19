package com.group1.wired.controllers;

import com.group1.wired.dto.FriendListDTO;
import com.group1.wired.dto.PendingRequestsDTO;
import org.springframework.web.bind.annotation.*;
import com.group1.wired.entities.FriendConnection;
import com.group1.wired.dto.FriendRequestDTO;
import com.group1.wired.service.FriendService;

import java.util.List;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/api/friends")
public class FriendsController {

    private final FriendService friendService;

    public FriendsController(FriendService friendService) {
        this.friendService = friendService;
    }

    @PostMapping("/add")
    public FriendConnection createFriendConnection(@RequestBody FriendRequestDTO request) {
        return friendService.createFriendConnection(
                request.getRequesterUserId(),
                request.getTargetFriendCode(),
                request.getStatus()
        );
    }

    @GetMapping("/list/{userId}")
    public List<FriendListDTO> getFriendList(@PathVariable Long userId) {
        return friendService.getFriendList(userId)
                .stream()
                .map(FriendListDTO::new)
                .toList();
    }
    @GetMapping("/requests/{userId}")
    public List<PendingRequestsDTO> getPendingRequests(@PathVariable Long userId) {
        return friendService.getPendingRequests(userId)
                .stream()
                .map(PendingRequestsDTO::new)
                .toList();
    }

    @PutMapping("/requests/accept/{connectionId}")
    public FriendConnection acceptFriendRequest(@PathVariable Long connectionId) {
        return friendService.acceptFriendRequest(connectionId);
    }

    @PutMapping("/requests/decline/{connectionId}")
    public FriendConnection declineFriendRequest(@PathVariable Long connectionId) {
        return friendService.declineFriendRequest(connectionId);
    }
}

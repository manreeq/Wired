package com.group1.wired.controllers;

import org.springframework.web.bind.annotation.*;
import com.group1.wired.entities.FriendConnection;
import com.group1.wired.dto.FriendRequestDTO;
import com.group1.wired.service.FriendService;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/api/friends")
public class FriendsController {

    private final FriendService friendService;

    public FriendsController(FriendService friendService) {
        this.friendService = friendService;
    }

    @PostMapping
    public FriendConnection createFriendConnection(@RequestBody FriendRequestDTO request) {
        return friendService.createFriendConnection(
                request.getRequesterUserId(),
                request.getTargetFriendCode(),
                request.getStatus()
        );
    }
}

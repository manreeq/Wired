package com.group1.wired.controllers;

import com.group1.wired.dto.FriendListDTO;
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

//    @PostMapping("/incoming")
//    public FriendConnection listFriendRequests(@RequestBody FriendListDTO request) {
//        return friendService.getPendingRequests(
//                request.getTargetFriendCode()
//        );
//    }

//    @PostMapping("/list/{userId}")
//    public List<FriendConnection> listFriendRequests(@RequestBody FriendListDTO request) {
//        return friendService.getFriendList(
//                request.getRequester(),
//                request.getRequester()
//        );
//    }

    @GetMapping("/list/{userId}")
    public List<FriendListDTO> getFriendList(@PathVariable Long userId) {
        return friendService.getFriendList(userId)
                .stream()
                .map(FriendListDTO::new)
                .toList();
    }

}

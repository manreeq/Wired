package com.group1.wired.controllers;

import com.group1.wired.dto.LiveActivityDTO;
import com.group1.wired.service.SpotifyPollingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/api/feed")
public class FeedController {

    private final SpotifyPollingService pollingService;

    @Autowired
    public FeedController(SpotifyPollingService pollingService) {
        this.pollingService = pollingService;
    }

    // Returns the current live playback state for all users (for initial page load)
    @GetMapping("/live")
    public ResponseEntity<List<LiveActivityDTO>> getLiveFeed() {
        return ResponseEntity.ok(pollingService.getActiveBroadcasts());
    }
}

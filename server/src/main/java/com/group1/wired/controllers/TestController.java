package com.group1.wired.controllers;

import com.group1.wired.controllers.PlaybackStateDTO;
import com.group1.wired.service.SpotifyPollingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/test")
public class TestController {

    private final SpotifyPollingService pollingService;

    @Autowired
    public TestController(SpotifyPollingService pollingService) {
        this.pollingService = pollingService;
    }

    // Endpoint 1: Force a poll immediately
    @GetMapping("/force-poll")
    public ResponseEntity<String> forcePoll() {
        pollingService.pollCurrentlyPlaying();
        return ResponseEntity.ok("Poll triggered successfully. Check your server console for logs.");
    }

    // Endpoint 2: Peek inside the RAM cache
    @GetMapping("/cache")
    public ResponseEntity<Map<Long, PlaybackStateDTO>> viewLiveCache() {
        return ResponseEntity.ok(pollingService.getLivePlaybackState());
    }
}
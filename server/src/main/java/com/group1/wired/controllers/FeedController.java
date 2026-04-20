package com.group1.wired.controllers;

import com.group1.wired.dto.LiveActivityDTO;
import com.group1.wired.entities.ListeningActivity;
import com.group1.wired.repositories.ListeningActivityRepository;
import com.group1.wired.service.SpotifyPollingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/api/feed")
public class FeedController {

    private final SpotifyPollingService pollingService;
    private final ListeningActivityRepository listeningActivityRepository;

    @Autowired
    public FeedController(SpotifyPollingService pollingService,
                          ListeningActivityRepository listeningActivityRepository) {
        this.pollingService = pollingService;
        this.listeningActivityRepository = listeningActivityRepository;
    }

    // Returns the current live playback state for all users (for initial page load)
    @GetMapping("/live")
    public ResponseEntity<List<LiveActivityDTO>> getLiveFeed() {
        return ResponseEntity.ok(pollingService.getActiveBroadcasts());
    }

    // Returns the logged-in user's past listening history.
    // ?limit=5 or ?limit=10 fetches a limited set; any other value (e.g. "all") returns everything.
    @GetMapping("/history/{userId}")
    public ResponseEntity<List<LiveActivityDTO>> getListeningHistory(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "5") String limit) {

        List<ListeningActivity> activities;

        if (limit.equalsIgnoreCase("all")) {
            activities = listeningActivityRepository.findByUser_UserIDOrderByTimestampDesc(userId);
        } else {
            int count;
            try {
                count = Integer.parseInt(limit);
            } catch (NumberFormatException e) {
                count = 5; // safe fallback
            }
            activities = listeningActivityRepository.findByUser_UserIDOrderByTimestampDesc(
                    userId, PageRequest.of(0, count));
        }

        List<LiveActivityDTO> dtos = activities.stream()
                .map(la -> new LiveActivityDTO(
                        la.getUser().getUserID(),
                        la.getUser().getDisplayName(),
                        la.getUser().getProfilePictureURL(),
                        la.getSong().getSongName(),
                        la.getSong().getAlbumArtUrl(),
                        la.getSong().getSpotifyTrackId(),
                        false   // isPlaying = false; these are past listens
                ))
                .collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }
}

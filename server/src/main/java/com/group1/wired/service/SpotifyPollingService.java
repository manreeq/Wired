package com.group1.wired.service;

import com.group1.wired.components.SpotifyDataRetrievalEngine;
import com.group1.wired.controllers.PlaybackStateDTO;
import com.group1.wired.entities.User;
import com.group1.wired.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SpotifyPollingService {

    private final UserRepository userRepository;
    private final AuthService authService;
    private final SpotifyDataRetrievalEngine spotifyEngine;
    private final ParseService parseService;

    // RAM memory of live states
    private final ConcurrentHashMap<Long, PlaybackStateDTO> livePlaybackState = new ConcurrentHashMap<>();

    @Autowired
    public SpotifyPollingService(UserRepository userRepository,
            AuthService authService,
            SpotifyDataRetrievalEngine spotifyEngine,
            ParseService parseService) {
        this.userRepository = userRepository;
        this.authService = authService;
        this.spotifyEngine = spotifyEngine;
        this.parseService = parseService;
    }

    public ConcurrentHashMap<Long, PlaybackStateDTO> getLivePlaybackState() {
        return livePlaybackState;
    }

    @Scheduled(fixedRateString = "${spotify.polling.rate:10000}")
    public void pollCurrentlyPlaying() {
        List<User> users = userRepository.findAll();
        for (User user : users) {
            try {
                // Get valid access token
                String token = authService.getValidAccessToken(user);

                // Fetch raw JSON
                String json = spotifyEngine.fetchCurrentlyPlaying(token);

                // Parse to DTO
                PlaybackStateDTO newDto = parseService.parseCurrentlyPlayingJson(json);
                Long userId = user.getUserID();

                PlaybackStateDTO cachedState = livePlaybackState.get(userId);

                // No Music / Paused
                if (newDto.getTrackId() == null || !newDto.isPlaying()) {
                    // Update the cache so the frontend knows they paused.
                    livePlaybackState.put(userId, newDto);
                    continue;
                }

                // New Song or First time logic
                if (cachedState == null || cachedState.getTrackId() == null
                        || !cachedState.getTrackId().equals(newDto.getTrackId())) {
                    // Start fresh
                    livePlaybackState.put(userId, newDto);
                    System.out.println("User " + userId + " started a new song: " + newDto.getTrackId());
                }
                // Same song still playing
                else {
                    // Update progress in cache
                    cachedState.setProgressMs(newDto.getProgressMs());

                    // Check threshold (30 seconds = 30000 ms) and prevent multiple logs
                    if (cachedState.getProgressMs() >= 30000 && !cachedState.isHasBeenLogged()) {
                        cachedState.setHasBeenLogged(true);
                        // Yet to-do is logic to map the trackId to a Song entity and save to
                        // ListeningActivityRepository
                        System.out.println("User " + userId + " hit 30s threshold! Flagging as logged for: "
                                + cachedState.getTrackId());
                    }
                }

            } catch (Exception e) {
                System.err.println("Error polling for user " + user.getUserID() + ": " + e.getMessage());
            }
        }
    }
}

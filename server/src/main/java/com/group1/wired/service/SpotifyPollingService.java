package com.group1.wired.service;

import com.group1.wired.components.SpotifyDataRetrievalEngine;
import com.group1.wired.controllers.PlaybackStateDTO;
import com.group1.wired.dto.LiveActivityDTO;
import com.group1.wired.entities.ListeningActivity;
import com.group1.wired.entities.Song;
import com.group1.wired.entities.User;
import com.group1.wired.repositories.ListeningActivityRepository;
import com.group1.wired.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
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
    private final ListeningActivityRepository listeningActivityRepository;
    private final SimpMessagingTemplate messagingTemplate;

    // RAM memory of live states
    private final ConcurrentHashMap<Long, PlaybackStateDTO> livePlaybackState = new ConcurrentHashMap<>();

    @Autowired
    public SpotifyPollingService(UserRepository userRepository,
            AuthService authService,
            SpotifyDataRetrievalEngine spotifyEngine,
            ParseService parseService,
            ListeningActivityRepository listeningActivityRepository,
            SimpMessagingTemplate messagingTemplate) {
        this.userRepository = userRepository;
        this.authService = authService;
        this.spotifyEngine = spotifyEngine;
        this.parseService = parseService;
        this.listeningActivityRepository = listeningActivityRepository;
        this.messagingTemplate = messagingTemplate;
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

                    // Fetch song details to broadcast to feed
                    Song newSong = parseService.parseAndSaveSong(token, newDto.getTrackId());
                    LiveActivityDTO activityDto = new LiveActivityDTO(
                            userId,
                            user.getDisplayName(),
                            user.getProfilePictureURL(),
                            newSong.getSongName(),
                            newSong.getAlbumArtUrl(),
                            newSong.getSpotifyTrackId()
                    );
                    messagingTemplate.convertAndSend("/topic/feed", activityDto);
                }
                // Same song still playing
                else {
                    // Update progress in cache
                    cachedState.setProgressMs(newDto.getProgressMs());

                    // Check threshold (30 seconds = 30000 ms) and prevent multiple logs
                    if (cachedState.getProgressMs() >= 30000 && !cachedState.isHasBeenLogged()) {
                    	// Fetch or create the Song entity using your ParseService
                        Song song = parseService.parseAndSaveSong(token, cachedState.getTrackId());
                        
                        // Create new ListeningActivity with the User and Song
                        ListeningActivity newActivity = new ListeningActivity(user, song);
                        
                        // Save to db
                        listeningActivityRepository.save(newActivity);
                        
                        // Mark as logged so it won't trigger again on the next poll
                        cachedState.setHasBeenLogged(true);
                        
                        System.out.println("User " + userId + " hit 30s threshold! Officially logged to DB: "
                                + song.getSongName());
                    }
                }

            } catch (Exception e) {
                System.err.println("Error polling for user " + user.getUserID() + ": " + e.getMessage());
            }
        }
    }
}

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

import java.util.ArrayList;
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

    // RAM memory of latest live activity per user (for initial page load)
    private final ConcurrentHashMap<Long, LiveActivityDTO> latestLiveActivities = new ConcurrentHashMap<>();

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

    public List<LiveActivityDTO> getActiveBroadcasts() {
        return new ArrayList<>(latestLiveActivities.values());
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
                    // Check if they JUST paused during this poll
                    if (cachedState != null && cachedState.isPlaying() && cachedState.getTrackId() != null) {
                        livePlaybackState.put(userId, newDto);

                        Song song = parseService.parseAndSaveSongFromPlaybackJson(token, json);
                        LiveActivityDTO activityDto = new LiveActivityDTO(
                                userId,
                                user.getDisplayName(),
                                user.getProfilePictureURL(),
                                song.getSongName(),
                                song.getAlbumArtUrl(),
                                song.getSpotifyTrackId(),
                                false);
                        latestLiveActivities.put(userId, activityDto);
                        messagingTemplate.convertAndSend("/topic/feed", activityDto);
                    } else {
                        livePlaybackState.put(userId, newDto);
                    }
                    continue;
                }

                // New Song, First time logic, or Unpausing the same song
                if (cachedState == null || cachedState.getTrackId() == null
                        || !cachedState.getTrackId().equals(newDto.getTrackId())
                        || (!cachedState.isPlaying() && newDto.isPlaying())) {
                    // Start fresh
                    livePlaybackState.put(userId, newDto);
                    System.out.println("User " + userId + " started a new song: " + newDto.getTrackId());

                    // Fetch song details to broadcast to feed
                    Song newSong = parseService.parseAndSaveSongFromPlaybackJson(token, json);
                    LiveActivityDTO activityDto = new LiveActivityDTO(
                            userId,
                            user.getDisplayName(),
                            user.getProfilePictureURL(),
                            newSong.getSongName(),
                            newSong.getAlbumArtUrl(),
                            newSong.getSpotifyTrackId(),
                            true);
                    latestLiveActivities.put(userId, activityDto);
                    messagingTemplate.convertAndSend("/topic/feed", activityDto);
                }
                // Same song still playing
                else {
                    // Update progress in cache
                    cachedState.setProgressMs(newDto.getProgressMs());

                    // Check threshold (30 seconds = 30000 ms) and prevent multiple logs
                    if (cachedState.getProgressMs() >= 30000 && !cachedState.isHasBeenLogged()) {
                        // Fetch or create the Song entity using your ParseService
                    	Song song = parseService.parseAndSaveSongFromPlaybackJson(token, json);

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

package com.group1.wired.controllers;

import com.group1.wired.entities.Album;
import com.group1.wired.entities.Playlist;
import com.group1.wired.entities.Song;
import com.group1.wired.entities.User;
import com.group1.wired.repositories.UserRepository;
import com.group1.wired.service.AuthService;
import com.group1.wired.service.ParseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/parse")
public class SpotifyParseController {

    private final ParseService parseService;
    private final AuthService authService;
    private final UserRepository userRepository;

    @Autowired
    public SpotifyParseController(ParseService parseService, AuthService authService, UserRepository userRepository) {
        this.parseService = parseService;
        this.authService = authService;
        this.userRepository = userRepository;
    }

    @PostMapping("/song/{spotifyTrackId}")
    public ResponseEntity<?> parseSong(
            @RequestHeader("User-Id") String spotifyUserId,
            @PathVariable String spotifyTrackId) {
        try {
            String accessToken = getValidToken(spotifyUserId);
            Song song = parseService.parseAndSaveSong(accessToken, spotifyTrackId);
            return ResponseEntity.ok(song);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to parse song: " + e.getMessage());
        }
    }

    @PostMapping("/album/{spotifyAlbumId}")
    public ResponseEntity<?> parseAlbum(
            @RequestHeader("User-Id") String spotifyUserId,
            @PathVariable String spotifyAlbumId) {
        try {
            String accessToken = getValidToken(spotifyUserId);
            Album album = parseService.parseAndSaveAlbum(accessToken, spotifyAlbumId);
            return ResponseEntity.ok(album);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to parse album: " + e.getMessage());
        }
    }

    @PostMapping("/playlist/{spotifyPlaylistId}")
    public ResponseEntity<?> parsePlaylist(
            @RequestHeader("User-Id") String spotifyUserId,
            @PathVariable String spotifyPlaylistId) {
        try {
            String accessToken = getValidToken(spotifyUserId);
            Playlist playlist = parseService.parseAndSavePlaylist(accessToken, spotifyPlaylistId);
            return ResponseEntity.ok(playlist);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to parse playlist: " + e.getMessage());
        }
    }

    private String getValidToken(String spotifyUserId) {
        User user = userRepository.findBySpotifyURI(spotifyUserId)
                .orElseThrow(() -> new RuntimeException("User not found: " + spotifyUserId));
        return authService.getValidAccessToken(user);
    }
}
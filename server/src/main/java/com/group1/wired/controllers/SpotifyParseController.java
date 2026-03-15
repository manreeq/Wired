package com.group1.wired.controllers;

import com.group1.wired.entities.Album;
import com.group1.wired.entities.Playlist;
import com.group1.wired.entities.Song;
import com.group1.wired.service.ParseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/parse")
public class SpotifyParseController {

    private final ParseService parseService;

    @Autowired
    public SpotifyParseController(ParseService parseService) {
        this.parseService = parseService;
    }

    @PostMapping("/song/{spotifyTrackId}")
    public ResponseEntity<?> parseSong(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable String spotifyTrackId) {
        try {
            String accessToken = extractToken(authHeader);
            Song song = parseService.parseAndSaveSong(accessToken, spotifyTrackId);
            return ResponseEntity.ok(song);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to parse song: " + e.getMessage());
        }
    }

    @PostMapping("/album/{spotifyAlbumId}")
    public ResponseEntity<?> parseAlbum(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable String spotifyAlbumId) {
        try {
            String accessToken = extractToken(authHeader);
            Album album = parseService.parseAndSaveAlbum(accessToken, spotifyAlbumId);
            return ResponseEntity.ok(album);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to parse album: " + e.getMessage());
        }
    }

    @PostMapping("/playlist/{spotifyPlaylistId}")
    public ResponseEntity<?> parsePlaylist(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable String spotifyPlaylistId) {
        try {
            String accessToken = extractToken(authHeader);
            Playlist playlist = parseService.parseAndSavePlaylist(accessToken, spotifyPlaylistId);
            return ResponseEntity.ok(playlist);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to parse playlist: " + e.getMessage());
        }
    }

    private String extractToken(String authHeader) {
    	// Remove Bearer prefix
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        throw new IllegalArgumentException("Missing or malformed Authorization header. Expected: Bearer <token>");
    }
}
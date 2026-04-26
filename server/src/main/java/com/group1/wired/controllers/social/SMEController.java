package com.group1.wired.controllers.social;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;

import com.group1.wired.components.social.SocialMediaEngine;
import com.group1.wired.entities.*;
import com.group1.wired.repositories.SongPostRepository;
import com.group1.wired.repositories.AlbumPostRepository;
import com.group1.wired.repositories.PlaylistPostRepository;

import com.group1.wired.dto.CommentDTO;
import com.group1.wired.dto.ReactionDTO;
import com.group1.wired.dto.InteractionRequestDTO;
import com.group1.wired.dto.CreatePostRequestDTO;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.group1.wired.repositories.SongArtistRepository;
import com.group1.wired.repositories.AlbumArtistRepository;
import java.util.Map;

@CrossOrigin(origins = "*", allowedHeaders = "*") // allows React talk to this controller
@RestController
@RequestMapping("/api/posts")
public class SMEController {

    private final SocialMediaEngine socialMediaEngine;
    private final SongPostRepository songPostRepository;
    private final AlbumPostRepository albumPostRepository;
    private final PlaylistPostRepository playlistPostRepository;
    
    private final SongArtistRepository songArtistRepository;
    private final AlbumArtistRepository albumArtistRepository;
    

    public SMEController(SocialMediaEngine socialMediaEngine,
            SongPostRepository songPostRepository,
            AlbumPostRepository albumPostRepository,
            PlaylistPostRepository playlistPostRepository,
            SongArtistRepository songArtistRepository,
            AlbumArtistRepository albumArtistRepository) {
        this.socialMediaEngine = socialMediaEngine;
        this.songPostRepository = songPostRepository;
        this.albumPostRepository = albumPostRepository;
        this.playlistPostRepository = playlistPostRepository;
        this.songArtistRepository = songArtistRepository;
        this.albumArtistRepository = albumArtistRepository;
    }

    // Feed History
    @GetMapping("/feed")
    public ResponseEntity<List<Post>> getFeed() {
        List<Post> all = new ArrayList<>();
        all.addAll(songPostRepository.findAll());
        all.addAll(albumPostRepository.findAll());
        all.addAll(playlistPostRepository.findAll());
        all.sort(Comparator.comparing(Post::getTimestamp).reversed());
        return ResponseEntity.ok(all);
    }

    // Song Post
    @PostMapping("/song")
    public SongPost createSongPost(@RequestBody CreatePostRequestDTO request) {
        return socialMediaEngine.createSongPost(
                extractSpotifyId(request.getSpotifyUrl()),
                request.getContent(),
                request.getUserId());
    }

    // Album Post
    @PostMapping("/album")
    public AlbumPost createAlbumPost(@RequestBody CreatePostRequestDTO request) {
        return socialMediaEngine.createAlbumPost(
                extractSpotifyId(request.getSpotifyUrl()),
                request.getContent(),
                request.getUserId());
    }

    // Playlist Post
    @PostMapping("/playlist")
    public PlaylistPost createPlaylistPost(@RequestBody CreatePostRequestDTO request) {
        return socialMediaEngine.createPlaylistPost(
                extractSpotifyId(request.getSpotifyUrl()),
                request.getContent(),
                request.getUserId());
    }

    /**
     * Extracts the raw Spotify ID from a full URL or returns the input as-is if it's already an ID.
     * Handles formats like:
     *   https://open.spotify.com/track/4uLU6hMCjMI75M1A2tKUQC?si=...
     *   https://open.spotify.com/album/1234567890abc
     *   4uLU6hMCjMI75M1A2tKUQC  (raw ID, passed through unchanged)
     */
    private String extractSpotifyId(String input) {
        if (input == null) return null;
        String trimmed = input.trim();
        // If it contains open.spotify.com, extract the ID segment after the type
        if (trimmed.contains("open.spotify.com/")) {
            // Split on '?' first to drop query params, then grab the last path segment
            String withoutQuery = trimmed.split("\\?")[0];
            String[] parts = withoutQuery.split("/");
            return parts[parts.length - 1];
        }
        // Otherwise assume it's already a raw Spotify ID
        return trimmed;
    }
    
    // Fetch Artist for a Song Post
    @GetMapping("/songs/{songId}/artist")
    public ResponseEntity<Map<String, String>> getSongArtist(@PathVariable Long songId) {
        List<SongArtist> artists = songArtistRepository.findBySong_SongId(songId);
        String artistName = artists.isEmpty() ? "Unknown Artist" : artists.get(0).getArtist().getArtistName();
        return ResponseEntity.ok(Map.of("artistName", artistName));
    }

    // Fetch Artist for an Album Post
    @GetMapping("/albums/{albumId}/artist")
    public ResponseEntity<Map<String, String>> getAlbumArtist(@PathVariable Long albumId) {
        List<AlbumArtist> artists = albumArtistRepository.findByAlbum_AlbumId(albumId);
        String artistName = artists.isEmpty() ? "Unknown Artist" : artists.get(0).getArtist().getArtistName();
        return ResponseEntity.ok(Map.of("artistName", artistName));
    }
    
    // Add Comment
    @PostMapping("/{postId}/comments")
    public ResponseEntity<CommentDTO> addComment(
            @PathVariable Long postId, 
            @RequestBody InteractionRequestDTO request) {
        CommentDTO comment = socialMediaEngine.addComment(postId, request.getUserId(), request.getPayload());
        return ResponseEntity.ok(comment);
    }

    // Add Reaction
    @PostMapping("/{postId}/reactions")
    public ResponseEntity<ReactionDTO> addReaction(
            @PathVariable Long postId, 
            @RequestBody InteractionRequestDTO request) {
        ReactionDTO reaction = socialMediaEngine.addReaction(postId, request.getUserId(), request.getPayload());
        return ResponseEntity.ok(reaction);
    }
    
    // Get Comments for a Post
    @GetMapping("/{postId}/comments")
    public ResponseEntity<List<CommentDTO>> getComments(@PathVariable Long postId) {
        return ResponseEntity.ok(socialMediaEngine.getCommentsForPost(postId));
    }

    // Get Reactions for a Post
    @GetMapping("/{postId}/reactions")
    public ResponseEntity<List<ReactionDTO>> getReactions(@PathVariable Long postId) {
        return ResponseEntity.ok(socialMediaEngine.getReactionsForPost(postId));
    }
}
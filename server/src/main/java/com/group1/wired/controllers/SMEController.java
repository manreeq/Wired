package com.group1.wired.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.http.ResponseEntity;

import com.group1.wired.components.SocialMediaEngine;
import com.group1.wired.entities.*;
import com.group1.wired.repositories.SongPostRepository;
import com.group1.wired.repositories.AlbumPostRepository;
import com.group1.wired.repositories.PlaylistPostRepository;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@CrossOrigin(origins = "*", allowedHeaders = "*") // allows React talk to this controller
@RestController
@RequestMapping("/api/posts")
public class SMEController {

    private final SocialMediaEngine socialMediaEngine;
    private final SongPostRepository songPostRepository;
    private final AlbumPostRepository albumPostRepository;
    private final PlaylistPostRepository playlistPostRepository;

    public SMEController(SocialMediaEngine socialMediaEngine,
            SongPostRepository songPostRepository,
            AlbumPostRepository albumPostRepository,
            PlaylistPostRepository playlistPostRepository) {
        this.socialMediaEngine = socialMediaEngine;
        this.songPostRepository = songPostRepository;
        this.albumPostRepository = albumPostRepository;
        this.playlistPostRepository = playlistPostRepository;
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
                request.getMediaId(),
                request.getContent(),
                request.getUserId());
    }

    // Album Post
    @PostMapping("/album")
    public AlbumPost createAlbumPost(@RequestBody CreatePostRequestDTO request) {
        return socialMediaEngine.createAlbumPost(
                request.getMediaId(),
                request.getContent(),
                request.getUserId());
    }

    // 3. Playlist Post
    @PostMapping("/playlist")
    public PlaylistPost createPlaylistPost(@RequestBody CreatePostRequestDTO request) {
        return socialMediaEngine.createPlaylistPost(
                request.getMediaId(),
                request.getContent(),
                request.getUserId());
    }
}
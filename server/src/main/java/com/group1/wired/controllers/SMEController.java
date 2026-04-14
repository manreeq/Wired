package com.group1.wired.controllers;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.CrossOrigin;

import com.group1.wired.components.SocialMediaEngine;
import com.group1.wired.entities.*;

@CrossOrigin(origins = "*", allowedHeaders = "*") // allows React talk to this controller
@RestController
@RequestMapping("/api/posts")
public class SMEController {

    private final SocialMediaEngine socialMediaEngine;

    public SMEController(SocialMediaEngine socialMediaEngine) {
        this.socialMediaEngine = socialMediaEngine;
    }
    
    // Song Post
    @PostMapping("/song")
    public SongPost createSongPost(@RequestBody CreatePostRequestDTO request) {
        return socialMediaEngine.createSongPost(
            request.getMediaId(), 
            request.getContent(), 
            request.getUserId()
        );
    }

    // Album Post
    @PostMapping("/album")
    public AlbumPost createAlbumPost(@RequestBody CreatePostRequestDTO request) {
        return socialMediaEngine.createAlbumPost(
            request.getMediaId(), 
            request.getContent(), 
            request.getUserId()
        );
    }

    // 3. Playlist Post
    @PostMapping("/playlist")
    public PlaylistPost createPlaylistPost(@RequestBody CreatePostRequestDTO request) {
        return socialMediaEngine.createPlaylistPost(
            request.getMediaId(), 
            request.getContent(), 
            request.getUserId()
        );
    }
}
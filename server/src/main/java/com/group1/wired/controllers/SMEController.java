package com.group1.wired.controllers;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.group1.wired.components.SocialMediaEngine;
import com.group1.wired.entities.*;

@RestController
//TO-DO: may change
@RequestMapping("/api/posts")
public class SMEController {

	private final SocialMediaEngine socialMediaEngine;

    public SMEController(SocialMediaEngine socialMediaEngine) {
        this.socialMediaEngine = socialMediaEngine;
    }
    
    
    //temporary til frontend is implemented
    @PostMapping("/song")
    public SongPost createSongPost(
            @RequestParam Long songId, 
            @RequestParam String content, 
            @RequestParam Long userId) {
        return socialMediaEngine.createSongPost(songId, content, userId);
    }

    // 2. POST an Album
    @PostMapping("/album")
    public AlbumPost createAlbumPost(
            @RequestParam Long albumId, 
            @RequestParam String content, 
            @RequestParam Long userId) {
        return socialMediaEngine.createAlbumPost(albumId, content, userId);
    }

    // 3. POST a Playlist
    @PostMapping("/playlist")
    public PlaylistPost createPlaylistPost(
            @RequestParam Long playlistId, 
            @RequestParam String content, 
            @RequestParam Long userId) {
        return socialMediaEngine.createPlaylistPost(playlistId, content, userId);
    }
    
    
}

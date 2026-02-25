package com.group1.wired.rest;

import com.group1.wired.components.SpotifyDataRetrievalEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
public class SpotifyTestController {

    @Autowired
    private SpotifyDataRetrievalEngine spotifyEngine;

    // creates the URL: http://localhost:8080/api/test/spotify
    @GetMapping("/spotify")
    public String testSpotifyConnection() {
        
        // for testing, just paste a temporary token from the spotify site
        String myTemporaryToken = "paste-temporary-token-here"; 
        
        return spotifyEngine.fetchCurrentUserProfile(myTemporaryToken);
    }
}
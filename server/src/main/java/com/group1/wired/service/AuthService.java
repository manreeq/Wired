package com.group1.wired.service;

import com.group1.wired.entities.User;
import com.group1.wired.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.Optional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final RestTemplate restTemplate;

    @Autowired
    public AuthService(UserRepository userRepository, RestTemplate restTemplate) {
        this.userRepository = userRepository;
        this.restTemplate = restTemplate;
    }

    public String authenticateWithSpotify(String spotifyToken) {
        
        // STEP 1: Verify the token with Spotify
        // We are calling Spotify's specific endpoint for getting the current user's profile
        String spotifyApiUrl = "https://api.spotify.com/v1/me";
        
        // We have to put the token in the "Authorization" header, like a VIP pass
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(spotifyToken); 
        HttpEntity<String> entity = new HttpEntity<>("", headers);

        // This is where your backend actually sends the GET request to Spotify
        ResponseEntity<Map> response;
        try {
            response = restTemplate.exchange(spotifyApiUrl, HttpMethod.GET, entity, Map.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to verify Spotify token. It might be expired or invalid.");
        }

        // Extract the user data from Spotify's JSON response
        Map<String, Object> spotifyUser = response.getBody();
        if (spotifyUser == null) {
            throw new RuntimeException("Spotify returned an empty profile.");
        }

        String spotifyUserId = (String) spotifyUser.get("id");
        String displayName = (String) spotifyUser.get("display_name");
        
        // STEP 2: Check your database
        // We look for a user in the Wired database who has this specific Spotify ID
        Optional<User> existingUser = userRepository.findBySpotifyId(spotifyUserId);

        if (existingUser.isEmpty()) {
            // STEP 3: Register them!
            // This is their first time here, so we create a new row in the database
            User newUser = new User();
            newUser.setSpotifyId(spotifyUserId);
            newUser.setDisplayName(displayName);
            
            userRepository.save(newUser);
            System.out.println("New user joined Wired: " + displayName);
        } else {
            // STEP 4: Log them in!
            System.out.println("Welcome back, " + displayName);
        }

        // STEP 5: Return a token for your app
        // For now, we will return a simple string combining your app name and their ID.
        // Later, you can upgrade this to a real JWT (JSON Web Token).
        return "wired-session-" + spotifyUserId; 
    }
}
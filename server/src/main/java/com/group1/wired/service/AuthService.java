package com.group1.wired.service;

import com.group1.wired.entities.AuthCredentials;
import com.group1.wired.entities.User;
import com.group1.wired.repositories.AuthCredentialsRepository;
import com.group1.wired.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Map;
import java.util.Optional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final AuthCredentialsRepository credentialsRepository;
    private final RestTemplate restTemplate;

    @Value("${spotify.api.client-id}")
    private String clientId;

    @Value("${spotify.api.client-secret}")	
    private String clientSecret;

    @Value("${spotify.redirect.uri}")
    private String redirectUri;

    @Autowired
    public AuthService(UserRepository userRepository, AuthCredentialsRepository credentialsRepository, RestTemplate restTemplate) {
        this.userRepository = userRepository;
        this.credentialsRepository = credentialsRepository;
        this.restTemplate = restTemplate;
    }

    public String processSpotifyLogin(String authCode) {
        // STEP 1: Swap the Auth Code for Access & Refresh Tokens
        Map<String, Object> tokenData = fetchTokensFromSpotify(authCode);
        
        String accessToken = (String) tokenData.get("access_token");
        String refreshToken = (String) tokenData.get("refresh_token");
        Integer expiresIn = (Integer) tokenData.get("expires_in");

        // STEP 2: Use the new Access Token to get the user's Spotify Profile
        Map<String, Object> spotifyProfile = fetchUserProfile(accessToken);
        String spotifyURI = (String) spotifyProfile.get("id");
        String displayName = (String) spotifyProfile.get("display_name");

        // STEP 3: Database Logic (Register or Login)
        Optional<User> existingUserOpt = userRepository.findBySpotifyURI(spotifyURI);
        User user;

        if (existingUserOpt.isEmpty()) {
            // New User Registration
            user = new User(spotifyURI, displayName);
            user.setJoinDate(LocalDateTime.now());
            user = userRepository.save(user); // Save to generate the ID
            
            // Create their Credentials entry
            AuthCredentials creds = new AuthCredentials();
            creds.setUser(user);
            creds.setAccessToken(accessToken);
            creds.setRefreshToken(refreshToken);
            creds.setTokenExpiresAt(LocalDateTime.now().plusSeconds(expiresIn));
            credentialsRepository.save(creds);
            
        } else {
            user = existingUserOpt.get();
            AuthCredentials creds = credentialsRepository.findByUser(user)
                    .orElse(new AuthCredentials());
            
            creds.setUser(user);
            creds.setAccessToken(accessToken);
            
            if (refreshToken != null) {
                creds.setRefreshToken(refreshToken);
            }
            creds.setTokenExpiresAt(LocalDateTime.now().plusSeconds(expiresIn));
            credentialsRepository.save(creds);
        }

        return "Successfully logged in as: " + user.getDisplayName();
    }


    // Helper methods
    
    private Map<String, Object> fetchTokensFromSpotify(String authCode) {
        String tokenUrl = "https://accounts.spotify.com/api/token";

        // Spotify requires Client ID and Secret to be Base64 encoded in the header
        String authString = clientId + ":" + clientSecret;
        String base64Auth = Base64.getEncoder().encodeToString(authString.getBytes());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.set("Authorization", "Basic " + base64Auth);

        // This is the "Form Data" required by Spotify
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("code", authCode);
        body.add("redirect_uri", redirectUri);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(tokenUrl, request, Map.class);
        return response.getBody();
    }

    private Map<String, Object> fetchUserProfile(String accessToken) {
        String profileUrl = "https://api.spotify.com/v1/me";
        
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        HttpEntity<String> entity = new HttpEntity<>("", headers);
        
        ResponseEntity<Map> response = restTemplate.exchange(profileUrl, HttpMethod.GET, entity, Map.class);
        return response.getBody();
    }
    
    public String getValidAccessToken(User user) {
        AuthCredentials creds = credentialsRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("No credentials found for user: " + user.getDisplayName()));

        // Add a 1-minute buffer. If it expires in less than a minute, refresh it now.
        if (creds.getTokenExpiresAt().isBefore(LocalDateTime.now().plusMinutes(1))) {
            return executeTokenRefresh(creds);
        }

        return creds.getAccessToken();
    }

    private String executeTokenRefresh(AuthCredentials creds) {
        String tokenUrl = "https://accounts.spotify.com/api/token"; 

        String authString = clientId + ":" + clientSecret;
        String base64Auth = Base64.getEncoder().encodeToString(authString.getBytes());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.set("Authorization", "Basic " + base64Auth);

        // Spotify requires "grant_type=refresh_token" and the actual refresh token
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "refresh_token");
        body.add("refresh_token", creds.getRefreshToken());

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(tokenUrl, request, Map.class);
            Map<String, Object> responseBody = response.getBody();

            if (responseBody == null) {
                throw new RuntimeException("Empty response from Spotify token endpoint");
            }

            String newAccessToken = (String) responseBody.get("access_token");
            Integer expiresIn = (Integer) responseBody.get("expires_in");

            // Spotify doesn't always return a NEW refresh token, but if they do, we MUST update it
            if (responseBody.containsKey("refresh_token")) {
                creds.setRefreshToken((String) responseBody.get("refresh_token"));
            }

            // Update the entity and save to the database
            creds.setAccessToken(newAccessToken);
            creds.setTokenExpiresAt(LocalDateTime.now().plusSeconds(expiresIn));
            credentialsRepository.save(creds);

            return newAccessToken;

        } catch (Exception e) {
            throw new RuntimeException("Failed to refresh Spotify token: " + e.getMessage());
        }
    }
}
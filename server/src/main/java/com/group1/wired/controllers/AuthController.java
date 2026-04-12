package com.group1.wired.controllers;

import com.group1.wired.controllers.SpotifyLoginRequestDTO;
import com.group1.wired.service.AuthService;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/spotify")
    public ResponseEntity<Map<String, String>> loginWithSpotify(@RequestBody SpotifyLoginRequestDTO request) {
        try {
            // Take a Map instead of a String from the service
            Map<String, String> result = authService.processSpotifyLogin(request.getCode());
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            // Return errors as JSON so the frontend doesn't crash trying to parse it
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Spotify Login Failed: " + e.getMessage());
            
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
}
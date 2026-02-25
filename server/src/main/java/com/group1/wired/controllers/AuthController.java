package com.group1.wired.controllers;

import com.group1.wired.controllers.AuthRequestDTO;
import com.group1.wired.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/spotify")
    public ResponseEntity<String> authenticate(@RequestBody AuthRequestDTO request) {
        try {
            // Hand the Spotify token to the Service
            String appToken = authService.authenticateWithSpotify(request.getSpotifyToken());
            return ResponseEntity.ok(appToken);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to authenticate with Spotify");
        }
    }
}
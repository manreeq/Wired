package com.group1.wired.controllers;

import com.group1.wired.dto.SpotifyLoginRequestDTO;
import com.group1.wired.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://127.0.0.1:5173")
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/spotify")
    public ResponseEntity<String> loginWithSpotify(@RequestBody SpotifyLoginRequestDTO request) {
        try {
            String result = authService.processSpotifyLogin(request.getCode());
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Spotify Login Failed: " + e.getMessage());
        }
    }
}
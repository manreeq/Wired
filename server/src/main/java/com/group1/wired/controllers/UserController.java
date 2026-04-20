package com.group1.wired.controllers;

import com.group1.wired.dto.UserProfileDTO;
import com.group1.wired.entities.User;
import com.group1.wired.repositories.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUserProfile(@PathVariable Long id) {
        
        return userRepository.findById(id)
            .map(user -> {

                UserProfileDTO profileData = new UserProfileDTO(
                    user.getUserID(),
                    user.getDisplayName(), 
                    user.getProfilePictureURL()
                );
                
                return ResponseEntity.ok(profileData);
            })
            .orElse(ResponseEntity.notFound().build());
    }
}
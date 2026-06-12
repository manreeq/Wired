package com.group1.wired.controllers.user;

import com.group1.wired.dto.UserProfileDTO;
import com.group1.wired.entities.User;
import com.group1.wired.repositories.UserRepository;

import java.util.Map;

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
                    user.getProfilePictureURL(),
                    user.isHistoryPrivate()
                );
                
                return ResponseEntity.ok(profileData);
            })
            .orElse(ResponseEntity.notFound().build());
    }
    
    @PutMapping("/{id}/privacy")
    public ResponseEntity<?> updatePrivacy(@PathVariable Long id, @RequestBody Map<String, Boolean> payload) {
        return userRepository.findById(id).map(user -> {
            
            Boolean isPrivate = payload.get("isHistoryPrivate");
            
            if (isPrivate != null) {
                user.setHistoryPrivate(isPrivate);
                userRepository.save(user); 
            }
            
            return ResponseEntity.ok(Map.of("message", "Privacy updated successfully"));
            
        }).orElse(ResponseEntity.notFound().build());
    }
}
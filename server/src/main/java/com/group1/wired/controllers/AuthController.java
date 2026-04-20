package com.group1.wired.controllers;
import com.group1.wired.dto.AuthResponse;
import com.group1.wired.dto.SpotifyLoginRequestDTO;
import com.group1.wired.entities.User;
import com.group1.wired.service.AuthService;
import com.group1.wired.repositories.UserRepository;

import java.util.HashMap;
import java.util.Map;
import org.springframework.web.bind.annotation.CrossOrigin;

import com.group1.wired.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.crypto.SecretKey;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "https://iteration4.d2t0j7fh8av7wl.amplifyapp.com", allowCredentials = "true")
public class AuthController {

    private final AuthService authService;
    private final UserRepository userRepository; 
    
    @Autowired
    private JwtUtils jwtUtils;

    @Value("${JWT_SECRET}")
    private String jwtSecret;

    @Value("${com.group.project.cookie.expires-in}")
    private int cookieExpiresIn;

    @Autowired
    public AuthController(AuthService authService, UserRepository userRepository) {
        this.authService = authService;
        this.userRepository = userRepository;
    }

    @PostMapping("/spotify")
    public ResponseEntity<?> loginWithSpotify(
            @RequestBody SpotifyLoginRequestDTO request,
            HttpServletResponse response) {
        try {
            AuthResponse authResponse = authService.processSpotifyLogin(request.getCode());

            // set the jwt as an http-only cookie with SameSite=Lax so the browser accepts it
            response.setHeader("Set-Cookie",
                    "authToken=" + authResponse.getToken() +
                    "; Path=/; HttpOnly; Max-Age=" + cookieExpiresIn +
                    "; SameSite=None; Secure");

            // return json with all the fields the frontend needs
            Map<String, String> successResponse = new HashMap<>();
            successResponse.put("message", "Successfully logged in as: " + authResponse.getDisplayName());
            successResponse.put("userID", String.valueOf(authResponse.getUserID()));
            successResponse.put("displayName", authResponse.getDisplayName());
            successResponse.put("friendCode", authResponse.getFriendCode());
            successResponse.put("profilePicUrl", authResponse.getProfilePictureURL());

            return ResponseEntity.ok(successResponse);

        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Spotify Login Failed: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    // handles GET requests to the endpoint, /api/auth/me.  returns the currently logged in user's info
    @GetMapping("/me")
    public ResponseEntity<?> getMe(HttpServletRequest request) {
        try {
            Cookie[] cookies = request.getCookies();
            if (cookies == null) return ResponseEntity.status(401).body("Not logged in"); 
            
            String token = null; 
            for (Cookie cookie : cookies) {
                if ("authToken".equals(cookie.getName())) {
                    token = cookie.getValue();
                    break;
                }
            }

            if (token == null) return ResponseEntity.status(401).body("Not logged in");

            SecretKey key = jwtUtils.generateSecretKey(jwtSecret);
            Claims claims = jwtUtils.getPayload(token, key); 	

            Long userId = Long.parseLong(claims.getSubject());

            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User no longer exists"));

            Map<String, Object> result = new HashMap<>();
            result.put("userID", user.getUserID());
            result.put("displayName", user.getDisplayName());
            result.put("profilePicUrl", user.getProfilePictureURL()); 
            result.put("isHistoryPrivate", user.isHistoryPrivate());

            return ResponseEntity.ok(result);

        } catch (Exception e) {
            return ResponseEntity.status(401).body("Invalid or expired session");
        }
    }
    
    @DeleteMapping("/me")
    public ResponseEntity<?> deleteMyAccount(HttpServletRequest request, HttpServletResponse response) {
        try {
            Cookie[] cookies = request.getCookies();
            if (cookies == null) return ResponseEntity.status(401).body("Not logged in");

            String token = null;
            for (Cookie cookie : cookies) {
                if ("authToken".equals(cookie.getName())) {
                    token = cookie.getValue();
                    break;
                }
            }
            if (token == null) return ResponseEntity.status(401).body("Not logged in");

            SecretKey key = jwtUtils.generateSecretKey(jwtSecret);
            Claims claims = jwtUtils.getPayload(token, key);
            Long userId = Long.parseLong(claims.getSubject());

            authService.deleteAccount(userId);

            Cookie deleteCookie = new Cookie("authToken", null);
            deleteCookie.setMaxAge(0);
            deleteCookie.setPath("/");
            response.addCookie(deleteCookie);

            Map<String, String> result = new HashMap<>();
            result.put("message", "Account deleted");
            return ResponseEntity.ok(result);

        } catch (Exception e) {
            return ResponseEntity.status(400).body("Error deleting account: " + e.getMessage());
        }
    }
}
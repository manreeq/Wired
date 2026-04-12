package com.group1.wired.controllers;
import java.util.HashMap;
import com.group1.wired.dto.AuthResponse;
import com.group1.wired.dto.SpotifyLoginRequestDTO;
import com.group1.wired.service.AuthService;

import java.util.HashMap;
import java.util.Map;

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
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    @Autowired
    private JwtUtils jwtUtils;

    @Value("${JWT_SECRET}")
    private String jwtSecret;

    @Value("${com.group.project.cookie.expires-in}")
    private int cookieExpiresIn;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/spotify")
    public ResponseEntity<String> loginWithSpotify(
            @RequestBody SpotifyLoginRequestDTO request,
            HttpServletResponse response) {
        try {
            AuthResponse authResponse = authService.processSpotifyLogin(request.getCode());

            // Set the JWT as an HTTP-only cookie
            Cookie cookie = new Cookie("authToken", authResponse.getToken());
            cookie.setHttpOnly(true); //frontend cannot access cookie
            cookie.setPath("/");		//sent to all routes on the backend
            cookie.setMaxAge(cookieExpiresIn);	//persistence
            response.addCookie(cookie);

            return ResponseEntity.ok("Successfully logged in as: " + authResponse.getDisplayName());
        } catch (Exception e) {
            // Return errors as JSON so the frontend doesn't crash trying to parse it
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Spotify Login Failed: " + e.getMessage());
            
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    //handles GET requests to the endpoint, /api/auth/me.  returns the currently logged in user's info
    @GetMapping("/me")
    public ResponseEntity<?> getMe(HttpServletRequest request) {
        try {
        	//grabs all the cookies sent by the browser with this request
        	Cookie[] cookies = request.getCookies();
            if (cookies == null) return ResponseEntity.status(401).body("Not logged in"); // if no cookies, user is not logged in
            String token = null; //holds jwt token
            
            //loop through all the cookies and look for authtoken
            for (Cookie cookie : cookies) {
                if ("authToken".equals(cookie.getName())) {
                    token = cookie.getValue();
                    break;
                }
            }

            if (token == null) return ResponseEntity.status(401).body("Not logged in");

            //convert the secret string into a format the jwt library understands
            SecretKey key = jwtUtils.generateSecretKey(jwtSecret);
            Claims claims = jwtUtils.getPayload(token, key); 	// // verify the token and extract its data

            //return the user's info extracted from the token to the frontend
            Map<String, Object> result = new HashMap<>();
            result.put("userID", claims.getSubject());
            result.put("displayName", claims.get("displayName", String.class));
            return ResponseEntity.ok(result);

        } catch (Exception e) {
            return ResponseEntity.status(401).body("Invalid or expired session");
        }
    }
}
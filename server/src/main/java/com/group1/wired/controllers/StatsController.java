package com.group1.wired.controllers;

import com.group1.wired.dto.TopSongDTO;
import com.group1.wired.dto.TopArtistDTO;
import com.group1.wired.dto.TopAlbumDTO;

import com.group1.wired.entities.User;
import com.group1.wired.repositories.UserRepository;
import com.group1.wired.service.StatsService;
import com.group1.wired.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.crypto.SecretKey;
import java.util.List;

@RestController
@RequestMapping("/api/stats")
public class StatsController {

    private final StatsService statsService;
    private final UserRepository userRepository;

    @Autowired
    private JwtUtils jwtUtils;

    @Value("${JWT_SECRET}")
    private String jwtSecret;

    @Autowired
    public StatsController(StatsService statsService, UserRepository userRepository) {
        this.statsService = statsService;
        this.userRepository = userRepository;
    }

    //reads jwt cookie and returns the logged in user
    private User getCurrentUser(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) throw new RuntimeException("Not logged in");

        String token = null;
        for (Cookie cookie : cookies) {
            if ("authToken".equals(cookie.getName())) {
                token = cookie.getValue();
                break;
            }
        }

        if (token == null) throw new RuntimeException("Not logged in");

        //convert secret string into a format the jwt library understands
        SecretKey key = jwtUtils.generateSecretKey(jwtSecret);

        //verify the token and extract its data
        Claims claims = jwtUtils.getPayload(token, key);

        Long userID = Long.parseLong(claims.getSubject());
        return userRepository.findById(userID)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    //GET /api/stats/top-songs
    //returns the current user's top 5 most listened songs
    @GetMapping("/top-songs")
    public ResponseEntity<?> getTopSongs( @RequestParam(defaultValue = "month") String range, HttpServletRequest request) {
        try {
            User currentUser = getCurrentUser(request);

            List<TopSongDTO> topSongs = statsService.getTopSongs(currentUser, range);

            //failsafe; reject the request if the user hasnt listened to at least 5 distinct songs yet
            if (topSongs.size() < 5) {
                return ResponseEntity.badRequest()
                        .body("Not enough listening history. Listen to at least 5 songs to see your top songs.");
            }

            return ResponseEntity.ok(topSongs);

        } catch (Exception e) {
            return ResponseEntity.status(401).build();
        }
    }
    
    //GET /api/stats/top-artists
    //returns the current user's top 5 most listened artists
    @GetMapping("/top-artists")
    public ResponseEntity<?> getTopArtists(@RequestParam(defaultValue = "month") String range, HttpServletRequest request) {
    	try {
    		User currentUser = getCurrentUser(request);

    		List<TopArtistDTO> topArtists = statsService.getTopArtists(currentUser, range);

    		// reject if user hasnt listened to enough distinct artists yet
    		if (topArtists.size() < 5) {
    			return ResponseEntity.badRequest()
    					.body("Not enough listening history. Listen to at least 5 artists to see your top artists.");
    		}

    		return ResponseEntity.ok(topArtists);

    	} catch (Exception e) {
         return ResponseEntity.status(401).build();
     }
 }
    //GET /api/stats/listening-time
	//returns the current user's total listening time
	@GetMapping("/listening-time")
	public ResponseEntity<?> getTotalListeningTime(@RequestParam(defaultValue = "month") String range, HttpServletRequest request) {
		try {
				User currentUser = getCurrentUser(request);
				String listeningTime = statsService.getTotalListeningTime(currentUser, range);
				return ResponseEntity.ok(listeningTime);
		} catch (Exception e) {
			return ResponseEntity.status(401).build();
		}
	}
	//GET /api/stats/top-albums
	//returns the current user's top 5 most listened albums
	@GetMapping("/top-albums")
	public ResponseEntity<?> getTopAlbums(@RequestParam(defaultValue = "month") String range, HttpServletRequest request) {
	    try {
	        User currentUser = getCurrentUser(request);

	        List<TopAlbumDTO> topAlbums = statsService.getTopAlbums(currentUser, range);

	        // reject if user hasnt listened to enough distinct albums yet
	        if (topAlbums.size() < 5) {
	            return ResponseEntity.badRequest()
	                    .body("Not enough listening history. Listen to at least 5 albums to see your top albums.");
	        }

	        return ResponseEntity.ok(topAlbums);

	    } catch (Exception e) {
	        return ResponseEntity.status(401).build();
	    }
	}
}
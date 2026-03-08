package com.group1.wired.rest;

import com.group1.wired.components.SpotifyDataRetrievalEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
public class SpotifyTestController {

    @Autowired
    private SpotifyDataRetrievalEngine spotifyEngine;

    // creates the URL: http://localhost:8080/api/test/spotify
    @GetMapping("/spotify")
    public String testSpotifyConnection() {
        
        // for testing, just paste a temporary token from the spotify site
        String myTemporaryToken = "BQDUg2ztOdPIROar7j04q0sxCrg4W4JEUqXgF6RHbzwMfMT9OqWThBaae3jYBNRYeEthS17KN1drjj7kSRcc5buCVfP-zuHHvra6r3ZrOAm6U__Vq45jOHGAJqiXHzJzLrACkuMh6m-U2eUhGj4hvJC1Nx8xCxZN9w1DVQR5oQHRwFbFAD27vYg0nvq1AjMgGV-PMUQ2LgWaxk1CKviItZp_c3JJkVf1WntR-cnrGYSM0FYLrkjCTXgenOgxLDToGcj_bQQzR5P5JSwdX8JNE_y1L0KvseI_H1aukb9Tu-xgpM-UH2hKt6bNDbEjqQ7pqilsS_3BMICZvFFIwzus3KOWU1obBZiF75YaPXYX9R8f_xS_q0vPkblqSh1QnFSWyUsH6Ku66pubugU3ZTFUeAWuzhE\r\n"
        		+ ""; 
        
        return spotifyEngine.fetchCurrentUserProfile(myTemporaryToken);
    }
}
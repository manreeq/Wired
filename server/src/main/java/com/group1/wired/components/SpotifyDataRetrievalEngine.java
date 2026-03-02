package com.group1.wired.components;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import org.springframework.stereotype.Service;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import java.io.IOException;

@Service
public class SpotifyDataRetrievalEngine {

	private Retrofit retrofit;
    private SpotifyApiClient spotifyApi;


    public SpotifyDataRetrievalEngine() {
        this.retrofit = new Retrofit.Builder()
                .baseUrl("https://api.spotify.com/")
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();

        // links to the interface
        this.spotifyApi = retrofit.create(SpotifyApiClient.class);
    }

    public String fetchCurrentUserProfile(String temporaryAccessToken) {
        try {
            Response<String> response = spotifyApi.fetchCurrentUserProfile("Bearer " + temporaryAccessToken).execute();
            
            if (response.isSuccessful() && response.body() != null) {
                return response.body();
            }
            
            return "Error from Spotify.";
            
        } catch (IOException e) {
            return "Server Error: Failed to reach Spotify - " + e.getMessage();
        }
    }
	
}

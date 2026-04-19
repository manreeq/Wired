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

    public String fetchTrack(String accessToken, String trackId) {
        try {
            Response<String> response = spotifyApi.fetchTrack("Bearer " + accessToken, trackId).execute();
            if (response.isSuccessful() && response.body() != null) {
                return response.body();
            }
            throw new RuntimeException("Spotify error fetching track: " + response.code());
        } catch (IOException e) {
            throw new RuntimeException("Failed to reach Spotify: " + e.getMessage());
        }
    }

    public String fetchAlbum(String accessToken, String albumId) {
        try {
            Response<String> response = spotifyApi.fetchAlbum("Bearer " + accessToken, albumId).execute();
            if (response.isSuccessful() && response.body() != null) {
                return response.body();
            }
            throw new RuntimeException("Spotify error fetching album: " + response.code());
        } catch (IOException e) {
            throw new RuntimeException("Failed to reach Spotify: " + e.getMessage());
        }
    }

    public String fetchPlaylist(String accessToken, String playlistId) {
        try {
            Response<String> response = spotifyApi.fetchPlaylist("Bearer " + accessToken, playlistId).execute();
            if (response.isSuccessful() && response.body() != null) {
                return response.body();
            }
            throw new RuntimeException("Spotify error fetching playlist: " + response.code());
        } catch (IOException e) {
            throw new RuntimeException("Failed to reach Spotify: " + e.getMessage());
        }
    }

    public String fetchCurrentlyPlaying(String accessToken) {
        try {
            Response<String> response = spotifyApi.fetchCurrentlyPlaying("Bearer " + accessToken).execute();

            if (response.isSuccessful() && response.body() != null) {
                return response.body();
            }
            // HTTP 204: Request was successful, but nothing is playing rn
            else if (response.code() == 204) {
                return null;
            }

            throw new RuntimeException("Spotify error fetching current playback: " + response.code());

        } catch (IOException e) {
            throw new RuntimeException("Failed to reach Spotify: " + e.getMessage());
        }
    }
    
    //for fetching artists, specifically the profile picture
    public String fetchArtist(String accessToken, String artistId) {
        try {
            Response<String> response = spotifyApi.fetchArtist("Bearer " + accessToken, artistId).execute();
            if (response.isSuccessful() && response.body() != null) {
                return response.body();
            }
            throw new RuntimeException("Spotify error fetching artist: " + response.code());
        } catch (IOException e) {
            throw new RuntimeException("Failed to reach Spotify: " + e.getMessage());
        }
    }

}

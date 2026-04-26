package com.group1.wired.components.spotify;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;

public interface SpotifyApiClient {

    // lets retrofit know what the spotify URL should have
    @GET("v1/me")
    Call<String> fetchCurrentUserProfile(@Header("Authorization") String authorizationHeader);
    
    @GET("v1/tracks/{id}")
    Call<String> fetchTrack(@Header("Authorization") String authorizationHeader, @Path("id") String trackId);
 
    @GET("v1/albums/{id}")
    Call<String> fetchAlbum(@Header("Authorization") String authorizationHeader, @Path("id") String albumId);
 
    @GET("v1/playlists/{id}")
    Call<String> fetchPlaylist(@Header("Authorization") String authorizationHeader, @Path("id") String playlistId);
    
    // For polling engine
    @GET("v1/me/player/currently-playing")
    Call<String> fetchCurrentlyPlaying(@Header("Authorization") String authorizationHeader);
    
    //for getting artist picture
    @GET("v1/artists/{id}")
    Call<String> fetchArtist(@Header("Authorization") String authorizationHeader, @Path("id") String artistId);

}
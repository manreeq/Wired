package com.group1.wired.components;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;

public interface SpotifyApiClient {

    // lets retrofit know what the spotify URL should have
    @GET("v1/me")
    Call<String> fetchCurrentUserProfile(@Header("Authorization") String authorizationHeader);

}
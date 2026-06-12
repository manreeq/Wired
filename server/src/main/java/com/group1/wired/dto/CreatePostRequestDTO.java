package com.group1.wired.dto;

public class CreatePostRequestDTO {

    private String spotifyUrl; // Accepts a full Spotify URL or a raw Spotify ID
    private String content;
    private Long userId;

    public CreatePostRequestDTO() {}

    public String getSpotifyUrl() {
        return spotifyUrl;
    }

    public void setSpotifyUrl(String spotifyUrl) {
        this.spotifyUrl = spotifyUrl;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}

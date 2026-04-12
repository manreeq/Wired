package com.group1.wired.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class LiveActivityDTO {

    private Long userId;
    private String displayName;
    private String profilePictureURL;
    private String songTitle;
    private String albumArtUrl;
    private String spotifyTrackId;
    @JsonProperty("isPlaying")
    private boolean isPlaying;

    public LiveActivityDTO() {}

    public LiveActivityDTO(Long userId, String displayName, String profilePictureURL,
                           String songTitle, String albumArtUrl, String spotifyTrackId, boolean isPlaying) {
        this.userId = userId;
        this.displayName = displayName;
        this.profilePictureURL = profilePictureURL;
        this.songTitle = songTitle;
        this.albumArtUrl = albumArtUrl;
        this.spotifyTrackId = spotifyTrackId;
        this.isPlaying = isPlaying;
    }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }

    public String getProfilePictureURL() { return profilePictureURL; }
    public void setProfilePictureURL(String profilePictureURL) { this.profilePictureURL = profilePictureURL; }

    public String getSongTitle() { return songTitle; }
    public void setSongTitle(String songTitle) { this.songTitle = songTitle; }

    public String getAlbumArtUrl() { return albumArtUrl; }
    public void setAlbumArtUrl(String albumArtUrl) { this.albumArtUrl = albumArtUrl; }

    public String getSpotifyTrackId() { return spotifyTrackId; }
    public void setSpotifyTrackId(String spotifyTrackId) { this.spotifyTrackId = spotifyTrackId; }

    public boolean isPlaying() { return isPlaying; }
    public void setPlaying(boolean playing) { isPlaying = playing; }
}

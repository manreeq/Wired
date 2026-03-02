package com.group1.wired.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "playlists")
public class Playlist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long playlistId;

    @Column(nullable = false)
    private String spotifyPlaylistId = "None";

    // not a fk, will just be queried from playlist information
    @Column(nullable = false)
    private String userId = "None";

    @Column(nullable = false)
    private String playlistName = "None";

    @Column(nullable = false)
    private Boolean isHidden = false;

    protected Playlist() {}

    public Playlist(String spotifyPlaylistId, String userId, String playlistName, Boolean isHidden) {
        this.spotifyPlaylistId = spotifyPlaylistId;
        this.userId = userId;
        this.playlistName = playlistName;
        this.isHidden = isHidden;
    }

    public Long getPlaylistId() {
        return playlistId;
    }

    protected void setPlaylistId(Long playlistId) {
        this.playlistId = playlistId;
    }

    public String getSpotifyPlaylistId() {
        return spotifyPlaylistId;
    }

    public void setSpotifyPlaylistId(String spotifyPlaylistId) {
        this.spotifyPlaylistId = spotifyPlaylistId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPlaylistName() {
        return playlistName;
    }

    public void setPlaylistName(String playlistName) {
        this.playlistName = playlistName;
    }

    public Boolean getIsHidden() {
        return isHidden;
    }

    public void setIsHidden(Boolean isHidden) {
        this.isHidden = isHidden;
    }
}
package com.group1.wired.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "albums")
public class Album {
	
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column 
    private Long albumId;

    @Column(nullable = false) 
    private String spotifyAlbumId = "None";

    @Column(nullable = false) 
    private String albumName = "None";

    @Column(nullable = false) 
    private String albumArtUrl = "None";
    
    protected Album() {}

    public Album(String spotifyAlbumId, String albumName, String albumArtUrl) {
        this.spotifyAlbumId = spotifyAlbumId;
        this.albumName = albumName;
        this.albumArtUrl = albumArtUrl;
    }

    public Long getAlbumId() {
        return albumId;
    }

    protected void setAlbumId(Long albumId) {
        this.albumId = albumId;
    }

    public String getSpotifyAlbumId() {
        return spotifyAlbumId;
    }

    public void setSpotifyAlbumId(String spotifyAlbumId) {
        this.spotifyAlbumId = spotifyAlbumId;
    }

    public String getAlbumName() {
        return albumName;
    }

    public void setAlbumName(String albumName) {
        this.albumName = albumName;
    }

    public String getAlbumArtUrl() {
        return albumArtUrl;
    }

    public void setAlbumArtUrl(String albumArtUrl) {
        this.albumArtUrl = albumArtUrl;
    }
}
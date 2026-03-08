package com.group1.wired.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "artists")
public class Artist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long artistId;

    @Column(nullable = false)
    private String spotifyArtistId = "None";

    @Column(nullable = false)
    private String artistName = "None";

    @Column(nullable = true)
    private String primaryGenre = "None";

    protected Artist() {}

    public Artist(String spotifyArtistId, String artistName, String primaryGenre) {
        this.spotifyArtistId = spotifyArtistId;
        this.artistName = artistName;
        this.primaryGenre = primaryGenre;
    }

    public Long getArtistId() {
        return artistId;
    }

    protected void setArtistId(Long artistId) {
        this.artistId = artistId;
    }

    public String getSpotifyArtistId() {
        return spotifyArtistId;
    }

    public void setSpotifyArtistId(String spotifyArtistId) {
        this.spotifyArtistId = spotifyArtistId;
    }

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public String getPrimaryGenre() {
        return primaryGenre;
    }

    public void setPrimaryGenre(String primaryGenre) {
        this.primaryGenre = primaryGenre;
    }
}
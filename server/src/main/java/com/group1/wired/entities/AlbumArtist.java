package com.group1.wired.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "album_artists")
public class AlbumArtist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long albumArtistId;

    // fk linking to album
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "album_id", nullable = false)
    private Album album;

    // fk linking to artist
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "artist_id", nullable = false)
    private Artist artist;

    protected AlbumArtist() {}

    public AlbumArtist(Album album, Artist artist) {
        this.album = album;
        this.artist = artist;
    }

    public Long getAlbumArtistId() {
        return albumArtistId;
    }

    protected void setAlbumArtistId(Long albumArtistId) {
        this.albumArtistId = albumArtistId;
    }

    public Album getAlbum() {
        return album;
    }

    public void setAlbum(Album album) {
        this.album = album;
    }

    public Artist getArtist() {
        return artist;
    }

    public void setArtist(Artist artist) {
        this.artist = artist;
    }
}
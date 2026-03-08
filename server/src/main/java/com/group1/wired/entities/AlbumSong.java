package com.group1.wired.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "album_songs")
public class AlbumSong {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long albumSongId;

    // fk linking to album
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "album_id", nullable = false)
    private Album album;

    // fk linking to song
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "song_id", nullable = false)
    private Song song;

    protected AlbumSong() {}

    public AlbumSong(Album album, Song song) {
        this.album = album;
        this.song = song;
    }

    public Long getAlbumSongId() {
        return albumSongId;
    }

    protected void setAlbumSongId(Long albumSongId) {
        this.albumSongId = albumSongId;
    }

    public Album getAlbum() {
        return album;
    }

    public void setAlbum(Album album) {
        this.album = album;
    }

    public Song getSong() {
        return song;
    }

    public void setSong(Song song) {
        this.song = song;
    }
}
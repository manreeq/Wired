package com.group1.wired.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "song_artists")
public class SongArtist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long songArtistId;

    // fk linking to song
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "song_id", nullable = false)
    private Song song;

    // fk linking to artist
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "artist_id", nullable = false)
    private Artist artist;

    protected SongArtist() {}

    public SongArtist(Song song, Artist artist) {
        this.song = song;
        this.artist = artist;
    }

    public Long getSongArtistId() {
        return songArtistId;
    }

    protected void setSongArtistId(Long songArtistId) {
        this.songArtistId = songArtistId;
    }

    public Song getSong() {
        return song;
    }

    public void setSong(Song song) {
        this.song = song;
    }

    public Artist getArtist() {
        return artist;
    }

    public void setArtist(Artist artist) {
        this.artist = artist;
    }
}
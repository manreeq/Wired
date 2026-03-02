package com.group1.wired.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "playlist_songs")
public class PlaylistSong {

    // ID generated for the relationship itself
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long playlistSongId;

    // fk linking to playlist
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "playlist_id", nullable = false)
    private Playlist playlist;

    // fk linking to song
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "song_id", nullable = false)
    private Song song;

    protected PlaylistSong() {}

    public PlaylistSong(Playlist playlist, Song song) {
        this.playlist = playlist;
        this.song = song;
    }

    public Long getPlaylistSongId() {
        return playlistSongId;
    }

    protected void setPlaylistSongId(Long playlistSongId) {
        this.playlistSongId = playlistSongId;
    }

    public Playlist getPlaylist() {
        return playlist;
    }

    public void setPlaylist(Playlist playlist) {
        this.playlist = playlist;
    }

    public Song getSong() {
        return song;
    }

    public void setSong(Song song) {
        this.song = song;
    }
}
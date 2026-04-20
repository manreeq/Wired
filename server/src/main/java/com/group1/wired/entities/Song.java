package com.group1.wired.entities;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Table(name = "songs")
public class Song {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "songID")
    private Long songId;

    @Column(name = "spotifyTrackID", nullable = false)
    private String spotifyTrackId = "None";

    @Column(name = "songName", nullable = false)
    private String songName = "None";

    @Column(name = "albumArtURL", nullable = false)
    private String albumArtUrl = "None";
    
    @Column(name = "durationMs", nullable = true)
    private Long durationMs = 0L;

    // Direct reference to parent album — kept as a lazy FK so it doesn't
    // cause N+1 or LazyInitializationException issues in the WebSocket flow.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "album_id", nullable = true)
    private Album album;

    protected Song() {}

    public Song(String spotifyTrackId, String songName, String albumArtUrl, Album album, Long durationMs) {
        this.spotifyTrackId = spotifyTrackId;
        this.songName = songName;
        this.albumArtUrl = albumArtUrl;
        this.album = album;
        this.durationMs = durationMs;
    }

	public Long getSongId() {
		return songId;
	}

	protected void setSongId(Long songId) {
		this.songId = songId;
	}

	public String getSpotifyTrackId() {
		return spotifyTrackId;
	}

	protected void setSpotifyTrackId(String spotifyTrackId) {
		this.spotifyTrackId = spotifyTrackId;
	}

	public String getSongName() {
		return songName;
	}

	public void setSongName(String songName) {
		this.songName = songName;
	}

	public String getAlbumArtUrl() {
		return albumArtUrl;
	}

	public void setAlbumArtUrl(String albumArtUrl) {
		this.albumArtUrl = albumArtUrl;
	}

	public Album getAlbum() {
		return album;
	}

	public void setAlbum(Album album) {
		this.album = album;
	}
	
	public Long getDurationMs() {
	    return durationMs;
	}

	public void setDurationMs(Long durationMs) {
	    this.durationMs = durationMs;
	}

}

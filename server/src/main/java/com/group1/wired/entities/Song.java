package com.group1.wired.entities;

import jakarta.persistence.Table;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
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
    
    protected Song() {}

    public Song(String spotifyTrackId, String songName, String albumArtUrl) {
        this.spotifyTrackId = spotifyTrackId;
        this.songName = songName;
        this.albumArtUrl = albumArtUrl;
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
    
    

}

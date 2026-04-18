package com.group1.wired.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

@Entity
@Table(name = "playlist_posts")
@PrimaryKeyJoinColumn(name = "post_id") 
public class PlaylistPost extends Post {
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "playlist_id", nullable = false)
	@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
	private Playlist playlist;
	
	public PlaylistPost() {}
	
	public PlaylistPost(User user, String caption, Playlist playlist) { 
        super(user, "P", caption); 
        this.playlist = playlist;
    }

	public Playlist getPlaylist() {
		return playlist;
	}

	public void setPlaylist(Playlist playlist) {
		this.playlist = playlist;
	}
}
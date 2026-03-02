package com.group1.wired.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "song_posts")
@PrimaryKeyJoinColumn(name = "post_id") 	//links the table's id to the id in the Posts table
public class SongPost extends Post {
	
	@ManyToOne(fetch = FetchType.LAZY) 		// creates the fk connecting the post to a song
	@JoinColumn(name = "song_id", nullable = false)
	private Song song;
	
	protected SongPost() {}
	
	public SongPost(User user, String caption, Song song) { 
		
        super(user, "S", caption); 	// "super" passes user, S discriminator, and caption to the 
        this.song = song;		    // parent Post class
    }

	public Song getSong() {
		return song;
	}

	public void setSong(Song song) {
		this.song = song;
	}
}
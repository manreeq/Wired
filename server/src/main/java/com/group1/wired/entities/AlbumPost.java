package com.group1.wired.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "album_posts")
@PrimaryKeyJoinColumn(name = "post_id") 	//links the table's id to the id in the Posts table
public class AlbumPost extends Post {
	
	@ManyToOne(fetch = FetchType.LAZY) 		// creates the fk connecting the post to a song
	@JoinColumn(name = "album_id", nullable = false)
	private Album album;
	
	protected AlbumPost() {}
	
	public AlbumPost(User user, String caption, Album album) { 
		
        super(user, "A", caption); 	// "super" passes user, A discriminator, and caption to the 
        this.album = album;		    // parent Post class
    }

	public Album getAlbum() {
		return album;
	}

	public void setAlbum(Album album) {
		this.album = album;
	}
}
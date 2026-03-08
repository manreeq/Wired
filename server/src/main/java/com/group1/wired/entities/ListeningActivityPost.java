package com.group1.wired.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "listening_activity_posts")
@PrimaryKeyJoinColumn(name = "post_id") 
public class ListeningActivityPost extends Post {
	

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "activity_id", nullable = false)
	private ListeningActivity listeningActivity;
	
	protected ListeningActivityPost() {}
	
	public ListeningActivityPost(User user, String caption, ListeningActivity listeningActivity) { 
        super(user, "LA", caption); 	// uses the Post constructor to fill in those attributes
        this.listeningActivity = listeningActivity;
    }

	public ListeningActivity getListeningActivity() {
		return listeningActivity;
	}

	public void setListeningActivity(ListeningActivity listeningActivity) {
		this.listeningActivity = listeningActivity;
	}
}
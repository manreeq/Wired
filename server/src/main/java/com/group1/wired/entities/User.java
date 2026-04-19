package com.group1.wired.entities;
import org.hibernate.annotations.CreationTimestamp;

import java.util.List;
import java.util.UUID;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.LocalDateTime;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
@Table(name = "users")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "posts"})
public class User {
	
	@Id
	@GeneratedValue(strategy =  GenerationType.IDENTITY)
	@Column
	private Long userID;
	
	@NotNull 											//prevents null from being passed onto the database
	@Column(nullable = false) 							//the database itself does not allow null
	private String spotifyURI = "None";		 		//default value is None
	
	@NotNull 						
	@Column(nullable = false) 		
	private String displayName = "None"; 
	
	@Column(nullable = true) 		
	private String profilePictureURL = "None"; 
	
	@NotNull 						
	@CreationTimestamp										//creation timestamp at current time
    @Column(nullable = false, updatable = false) 		
	private LocalDateTime joinDate;
	
	@Column(name = "is_history_private", nullable = false, columnDefinition = "boolean default false")
	private boolean isHistoryPrivate = false;
	
	@Column(unique = true, nullable = false)
	private String friendCode;
	
	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Post> posts;
	
	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Reaction> reactions;

	
	protected User() {}
	
	public User(String spotifyURI, String displayName) { 	//constructor 
        this.spotifyURI = spotifyURI;
        this.displayName = displayName;
        this.friendCode = "WIRED-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase(); //create friendcode
    }

	public Long getUserID() {
		return userID;
	}

	protected void setUserID(Long userID) { //protected so only JPA can access this
		this.userID = userID;
	}

	public String getSpotifyURI() {
		return spotifyURI;
	}

	public void setSpotifyURI(String spotifyURI) {
		this.spotifyURI = spotifyURI;
	}

	public String getDisplayName() {
		return displayName;
	}
	
	public String getFriendCode() {
	    return friendCode;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getProfilePictureURL() {
		return profilePictureURL;
	}

	public void setProfilePictureURL(String profilePictureURL) {
		this.profilePictureURL = profilePictureURL;
	}

	public void setJoinDate(LocalDateTime joinDate) {
	    this.joinDate = joinDate;
	}
	
	public LocalDateTime getJoinDate() {
		return joinDate;
	}
	
	// helper methods for comments and reactions
    public void addComment(Comment comment) {
        comments.add(comment);
        comment.setUser(this);
    }

    public void removeComment(Comment comment) {
        comments.remove(comment);
        comment.setUser(null);
    }

    public void addReaction(Reaction reaction) {
        reactions.add(reaction);
        reaction.setUser(this);
    }

    public void removeReaction(Reaction reaction) {
        reactions.remove(reaction);
        reaction.setUser(null);
    }
}

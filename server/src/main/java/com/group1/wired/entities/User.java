package com.group1.wired.entities;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
@Table(name = "users")
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

	
	protected User() {}
	
	public User(String spotifyURI, String displayName) { 	//constructor 
        this.spotifyURI = spotifyURI;
        this.displayName = displayName;
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

	
	
}

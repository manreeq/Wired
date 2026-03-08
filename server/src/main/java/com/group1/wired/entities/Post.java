package com.group1.wired.entities;

import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
@Table(name = "posts")

@Inheritance(strategy = InheritanceType.JOINED) // identifies that post is a parent class; tells hibernate 
												// to create separate tables for children
public class Post {
	
	@Id
	@GeneratedValue(strategy =  GenerationType.IDENTITY)
	@Column
	private Long postID;
	
	@ManyToOne(fetch = FetchType.LAZY)					// foreign key; many posts can belong to one user
	@JoinColumn(name = "user_id", nullable = false)		//lazy fetchtype; only gets the actual post, not everything else that comes with the fk
	private User user;
	

	@NotNull 											// discriminator (S, P, LA, A)
	@Column(name = "post_type", nullable = false) 							
	private String postType;		 		
	
	@Column(nullable = true) 		
	private String caption; 					
	
	@NotNull 						
	@CreationTimestamp									
    @Column(nullable = false, updatable = false) 		
	private LocalDateTime timestamp;

	
	protected Post() {}
	
	public Post(User user, String postType, String caption) { 
        this.user = user;
        this.postType = postType;
        this.caption = caption;
    }

	public Long getPostID() {
		return postID;
	}

	protected void setPostID(Long postID) { 
		this.postID = postID;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getPostType() {
		return postType;
	}

	public void setPostType(String postType) {
		this.postType = postType;
	}

	public String getCaption() {
		return caption;
	}

	public void setCaption(String caption) {
		this.caption = caption;
	}

	public LocalDateTime getTimestamp() {
		return timestamp;
	}
}
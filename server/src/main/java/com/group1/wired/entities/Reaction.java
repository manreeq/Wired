package com.group1.wired.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "reactions")
public class Reaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long reactionId;

    // fk to post being reacted to
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    // fk to user who left reaction
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String reactionType = "None";

    protected Reaction() {}

    public Reaction(Post post, User user, String reactionType) {
        this.post = post;
        this.user = user;
        this.reactionType = reactionType;
    }

    public Long getReactionId() {
        return reactionId;
    }

    protected void setReactionId(Long reactionId) {
        this.reactionId = reactionId;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getReactionType() {
        return reactionType;
    }

    public void setReactionType(String reactionType) {
        this.reactionType = reactionType;
    }
}
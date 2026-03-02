package com.group1.wired.entities;

import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;
import jakarta.persistence.*;

@Entity
@Table(name = "listening_activities")
public class ListeningActivity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long activityId;

    @ManyToOne(fetch = FetchType.LAZY)		// fk linking to user who listened to the song
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)		// fk to the song that was played
    @JoinColumn(name = "song_id", nullable = false)
    private Song song;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime timestamp;

    
    protected ListeningActivity() {}


    public ListeningActivity(User user, Song song) {
        this.user = user;
        this.song = song;
    }

    public Long getActivityId() {
        return activityId;
    }


    protected void setActivityId(Long activityId) {
        this.activityId = activityId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Song getSong() {
        return song;
    }

    public void setSong(Song song) {
        this.song = song;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

}
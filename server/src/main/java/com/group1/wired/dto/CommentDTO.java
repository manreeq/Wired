package com.group1.wired.dto;

import java.time.LocalDateTime;

public class CommentDTO {
    private Long commentId;
    private Long postId;
    private Long userId;
    private String displayName;
    private String profilePicUrl;
    private String content;
    private LocalDateTime timestamp;

    public CommentDTO(Long commentId, Long postId, Long userId, String displayName, String profilePicUrl, String content, LocalDateTime timestamp) {
        this.commentId = commentId;
        this.postId = postId;
        this.userId = userId;
        this.displayName = displayName;
        this.profilePicUrl = profilePicUrl;
        this.content = content;
        this.timestamp = timestamp;
    }

    public Long getCommentId() { return commentId; }
    public void setCommentId(Long commentId) { this.commentId = commentId; }
    public Long getPostId() { return postId; }
    public void setPostId(Long postId) { this.postId = postId; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }
    public String getProfilePicUrl() { return profilePicUrl; }
    public void setProfilePicUrl(String profilePicUrl) { this.profilePicUrl = profilePicUrl; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}
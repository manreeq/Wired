package com.group1.wired.dto;

public class ReactionDTO {
    private Long reactionId;
    private Long postId;
    private Long userId;
    private String displayName;
    private String reactionType;

    public ReactionDTO(Long reactionId, Long postId, Long userId, String displayName, String reactionType) {
        this.reactionId = reactionId;
        this.postId = postId;
        this.userId = userId;
        this.displayName = displayName;
        this.reactionType = reactionType;
    }

    public Long getReactionId() { return reactionId; }
    public void setReactionId(Long reactionId) { this.reactionId = reactionId; }
    public Long getPostId() { return postId; }
    public void setPostId(Long postId) { this.postId = postId; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }
    public String getReactionType() { return reactionType; }
    public void setReactionType(String reactionType) { this.reactionType = reactionType; }
}
package com.group1.wired.controllers;

public class CreatePostRequestDTO {
    
    private Long mediaId; // 'mediaId' catch all for songId, albumId, or playlistId
    private String content;
    private Long userId;

    public CreatePostRequestDTO() {}

    public Long getMediaId() {
        return mediaId;
    }

    public void setMediaId(Long mediaId) {
        this.mediaId = mediaId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
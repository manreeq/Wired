package com.group1.wired.dto;

public class InteractionRequestDTO {
    private Long userId;
    private String payload; // Will hold either the comment text or the reaction emoji/type

    public InteractionRequestDTO() {}

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getPayload() { return payload; }
    public void setPayload(String payload) { this.payload = payload; }
}
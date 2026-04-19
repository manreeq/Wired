package com.group1.wired.dto;

public class UserProfileDTO {
    private String displayName;
    private String profilePicURL;

    public UserProfileDTO(String displayName, String profilePicURL) {
        this.displayName = displayName;
        this.profilePicURL = profilePicURL;
    }

    public String getDisplayName() { return displayName; }
    public String getProfilePicUrl() { return profilePicURL; }
    
    public void setDisplayName(String displayName) { this.displayName = displayName; }
    public void setProfilePicUrl(String profilePicURL) { this.profilePicURL = profilePicURL; }
}
package com.group1.wired.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserProfileDTO {
    private Long userId;
    private String displayName;
    private String profilePicURL;

    public UserProfileDTO(Long userId, String displayName, String profilePicURL) {
        this.userId = userId;
        this.displayName = displayName;
        this.profilePicURL = profilePicURL;
    }

//    public String getDisplayName() { return displayName; }
//    public String getProfilePicUrl() { return profilePicURL; }
//
//    public void setDisplayName(String displayName) { this.displayName = displayName; }
//    public void setProfilePicUrl(String profilePicURL) { this.profilePicURL = profilePicURL; }
}
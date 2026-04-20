package com.group1.wired.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserProfileDTO {
    private Long userId;
    private String displayName;
    private String profilePicUrl;
    @JsonProperty("isHistoryPrivate")
    private boolean isHistoryPrivate;

    public UserProfileDTO(Long userId, String displayName, String profilePicUrl, boolean isHistoryPrivate) {
        this.userId = userId;
        this.displayName = displayName;
        this.profilePicUrl = profilePicUrl;
        this.isHistoryPrivate = isHistoryPrivate;
    }

//    public String getDisplayName() { return displayName; }
//    public String getProfilePicUrl() { return profilePicURL; }
//
//    public void setDisplayName(String displayName) { this.displayName = displayName; }
//    public void setProfilePicUrl(String profilePicURL) { this.profilePicURL = profilePicURL; }
}
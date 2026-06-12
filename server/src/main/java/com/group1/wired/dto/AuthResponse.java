package com.group1.wired.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class AuthResponse {
    // storing User fields
    private Long userID;
    private String spotifyURI;
    private String displayName;
    private String profilePictureURL;
    private String friendCode;

    // JWT issued by backend
    private String token;
}
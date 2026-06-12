package com.group1.wired.dto;

public class TopArtistDTO {

    private String artistName;
    private String profilePictureUrl;
    private Long listenCount;

    public TopArtistDTO(String artistName, String profilePictureUrl, Long listenCount) {
        this.artistName = artistName;
        this.profilePictureUrl = profilePictureUrl;
        this.listenCount = listenCount;
    }

    public String getArtistName() { return artistName; }
    public String getProfilePictureUrl() { return profilePictureUrl; }
    public Long getListenCount() { return listenCount; }
}
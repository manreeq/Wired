package com.group1.wired.dto;

public class TopAlbumDTO {

    private String albumName;
    private String albumArtUrl;
    private Long listenCount;

    public TopAlbumDTO(String albumName, String albumArtUrl, Long listenCount) {
        this.albumName = albumName;
        this.albumArtUrl = albumArtUrl;
        this.listenCount = listenCount;
    }

    public String getAlbumName() { return albumName; }
    public String getAlbumArtUrl() { return albumArtUrl; }
    public Long getListenCount() { return listenCount; }
}

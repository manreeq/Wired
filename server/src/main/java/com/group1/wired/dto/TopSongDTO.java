package com.group1.wired.dto;

import java.util.List;

public class TopSongDTO {

    private String songName;
    private String albumArtUrl;
    private List<String> artists;
    private Long listenCount;

    public TopSongDTO(String songName, String albumArtUrl, List<String> artists, Long listenCount) {
        this.songName = songName;
        this.albumArtUrl = albumArtUrl;
        this.artists = artists;
        this.listenCount = listenCount;
    }

    public String getSongName() { return songName; }
    public String getAlbumArtUrl() { return albumArtUrl; }
    public List<String> getArtists() { return artists; }
    public Long getListenCount() { return listenCount; }
}

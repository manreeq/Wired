package com.group1.wired.dto;

public class PlaybackStateDTO {
    
    private boolean isPlaying;
    private String trackId;
    private long progressMs;
    private boolean hasBeenLogged;

    public PlaybackStateDTO() {
        this.isPlaying = false;
        this.trackId = null;
        this.progressMs = 0;
        this.hasBeenLogged = false;
    }

    public PlaybackStateDTO(boolean isPlaying, String trackId, long progressMs, boolean hasBeenLogged) {
        this.isPlaying = isPlaying;
        this.trackId = trackId;
        this.progressMs = progressMs;
        this.hasBeenLogged = hasBeenLogged;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public void setPlaying(boolean playing) {
        isPlaying = playing;
    }

    public String getTrackId() {
        return trackId;
    }

    public void setTrackId(String trackId) {
        this.trackId = trackId;
    }

    public long getProgressMs() {
        return progressMs;
    }

    public void setProgressMs(long progressMs) {
        this.progressMs = progressMs;
    }

    public boolean isHasBeenLogged() {
        return hasBeenLogged;
    }

    public void setHasBeenLogged(boolean hasBeenLogged) {
        this.hasBeenLogged = hasBeenLogged;
    }
}

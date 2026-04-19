package com.group1.wired.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.group1.wired.components.SpotifyDataRetrievalEngine;
import com.group1.wired.entities.Album;
import com.group1.wired.entities.AlbumArtist;
import com.group1.wired.entities.Artist;
import com.group1.wired.entities.Playlist;
import com.group1.wired.entities.Song;
import com.group1.wired.entities.SongArtist;
import com.group1.wired.repositories.AlbumArtistRepository;
import com.group1.wired.repositories.AlbumRepository;
import com.group1.wired.repositories.ArtistRepository;
import com.group1.wired.repositories.PlaylistRepository;
import com.group1.wired.repositories.SongArtistRepository;
import com.group1.wired.repositories.SongRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.group1.wired.controllers.PlaybackStateDTO;

@Service
public class ParseService {

    private final SpotifyDataRetrievalEngine spotifyEngine;
    private final SongRepository songRepository;
    private final AlbumRepository albumRepository;
    private final PlaylistRepository playlistRepository;
    private final ArtistRepository artistRepository;
    private final SongArtistRepository songArtistRepository;
    private final AlbumArtistRepository albumArtistRepository;
    private final ObjectMapper objectMapper;

    @Autowired
    public ParseService(SpotifyDataRetrievalEngine spotifyEngine,
    					SongRepository songRepository,
    					AlbumRepository albumRepository,
    					PlaylistRepository playlistRepository,
    					ArtistRepository artistRepository,
    					SongArtistRepository songArtistRepository,
    					AlbumArtistRepository albumArtistRepository) {
        this.spotifyEngine = spotifyEngine;
        this.songRepository = songRepository;
        this.albumRepository = albumRepository;
        this.playlistRepository = playlistRepository;
        this.artistRepository = artistRepository;
        this.songArtistRepository = songArtistRepository;
        this.albumArtistRepository = albumArtistRepository;
        this.objectMapper = new ObjectMapper();
    }

    @Transactional
    public Song parseAndSaveSong(String accessToken, String spotifyTrackId) {
        return songRepository.findBySpotifyTrackId(spotifyTrackId).orElseGet(() -> {
            String json = spotifyEngine.fetchTrack(accessToken, spotifyTrackId);
            return parseSongFromJson(accessToken, json);
        });
    }

    private Song parseSongFromJson(String accessToken, String json) {
        try {
            JsonNode root = objectMapper.readTree(json); // Parse JSON response heirarchically into JsonNode object 

            String spotifyTrackId = root.path("id").asText();
            String songName = root.path("name").asText();

            // Album art is nested inside album object
            String albumArtUrl = root.path("album")
                    .path("images")
                    .path(0)
                    .path("url")
                    .asText("None");

            Song song = new Song(spotifyTrackId, songName, albumArtUrl);
            song = songRepository.save(song);

            JsonNode artistsNode = root.path("artists");
            for (JsonNode artistNode : artistsNode) {
                String artistSpotifyId = artistNode.path("id").asText();
                String artistName = artistNode.path("name").asText("Unknown");
                Artist artist = parseAndSaveArtist(artistSpotifyId, artistName);
                songArtistRepository.save(new SongArtist(song, artist));
            }

            return song;

        } catch (Exception e) {
            throw new RuntimeException("Failed to parse track JSON: " + e.getMessage());
        }
    }

    @Transactional
    public Album parseAndSaveAlbum(String accessToken, String spotifyAlbumId) {
        return albumRepository.findBySpotifyAlbumId(spotifyAlbumId).orElseGet(() -> {
            String json = spotifyEngine.fetchAlbum(accessToken, spotifyAlbumId);
            return parseAlbumFromJson(json);
        });
    }

    private Album parseAlbumFromJson(String json) {
        try {
            JsonNode root = objectMapper.readTree(json);

            String spotifyAlbumId = root.path("id").asText();
            String albumName = root.path("name").asText();
            String albumArtUrl = root.path("images").path(0).path("url").asText("None");

            Album album = new Album(spotifyAlbumId, albumName, albumArtUrl);
            album = albumRepository.save(album);

            JsonNode artistsNode = root.path("artists");
            for (JsonNode artistNode : artistsNode) {
                String artistSpotifyId = artistNode.path("id").asText();
                String artistName = artistNode.path("name").asText("Unknown");
                Artist artist = parseAndSaveArtist(artistSpotifyId, artistName);
                albumArtistRepository.save(new AlbumArtist(album, artist));
            }

            return album;

        } catch (Exception e) {
            throw new RuntimeException("Failed to parse album JSON: " + e.getMessage());
        }
    }
    
    @Transactional
    public Playlist parseAndSavePlaylist(String accessToken, String spotifyPlaylistId) {
        return playlistRepository.findBySpotifyPlaylistId(spotifyPlaylistId).orElseGet(() -> {
            String json = spotifyEngine.fetchPlaylist(accessToken, spotifyPlaylistId);
            return parsePlaylistFromJson(json);
        });
    }

    private Playlist parsePlaylistFromJson(String json) {
        try {
            JsonNode root = objectMapper.readTree(json);

            String spotifyPlaylistId = root.path("id").asText();
            String playlistName = root.path("name").asText();

            String ownerId = root.path("owner").path("id").asText("None");

            boolean isPublic = root.path("public").asBoolean(true);

            Playlist playlist = new Playlist(spotifyPlaylistId, ownerId, playlistName, !isPublic);
            return playlistRepository.save(playlist);

        } catch (Exception e) {
            throw new RuntimeException("Failed to parse playlist JSON: " + e.getMessage());
        }
    }

    // Used by parseSongFromJson and parseAlbumFromJson to upsert an artist in the database.
    // We extract artist names directly from the Track/Album JSON to avoid making extra Spotify API calls.
    private Artist parseAndSaveArtist(String spotifyArtistId, String artistName) {
        return artistRepository.findBySpotifyArtistId(spotifyArtistId).map(existingArtist -> {
            // Backfill name if artist was previously saved without one
            if ("Unknown".equals(existingArtist.getArtistName())) {
                existingArtist.setArtistName(artistName);
                return artistRepository.save(existingArtist);
            }
            return existingArtist;
        }).orElseGet(() -> {
            Artist artist = new Artist(spotifyArtistId, artistName, "None");
            return artistRepository.save(artist);
        });
    }

    public PlaybackStateDTO parseCurrentlyPlayingJson(String json) {
        if (json == null || json.trim().isEmpty()) {
            return new PlaybackStateDTO(); // Default no-content fallback
        }

        try {
            JsonNode root = objectMapper.readTree(json);

            JsonNode itemNode = root.path("item");
            if (itemNode.isMissingNode() || itemNode.isNull() || !itemNode.has("id")) {
                return new PlaybackStateDTO();
            }

            boolean isPlaying = root.path("is_playing").asBoolean(false);
            long progressMs = root.path("progress_ms").asLong(0);
            String trackId = itemNode.path("id").asText(null);

            return new PlaybackStateDTO(isPlaying, trackId, progressMs, false);

        } catch (Exception e) {
            throw new RuntimeException("Failed to parse currently playing JSON: " + e.getMessage());
        }
    }
}
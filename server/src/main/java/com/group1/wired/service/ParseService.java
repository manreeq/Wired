package com.group1.wired.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.group1.wired.components.SpotifyDataRetrievalEngine;
import com.group1.wired.entities.Album;
import com.group1.wired.entities.Artist;
import com.group1.wired.entities.Playlist;
import com.group1.wired.entities.Song;
import com.group1.wired.entities.SongArtist;
import com.group1.wired.repositories.AlbumRepository;
import com.group1.wired.repositories.ArtistRepository;
import com.group1.wired.repositories.PlaylistRepository;
import com.group1.wired.repositories.SongArtistRepository;
import com.group1.wired.repositories.SongRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ParseService {

    private final SpotifyDataRetrievalEngine spotifyEngine;
    private final SongRepository songRepository;
    private final AlbumRepository albumRepository;
    private final PlaylistRepository playlistRepository;
    private final ArtistRepository artistRepository;
    private final SongArtistRepository songArtistRepository;
    private final ObjectMapper objectMapper;

    @Autowired
    public ParseService(SpotifyDataRetrievalEngine spotifyEngine,
    					SongRepository songRepository,
    					AlbumRepository albumRepository,
    					PlaylistRepository playlistRepository,
    					ArtistRepository artistRepository,
    					SongArtistRepository songArtistRepository) {
        this.spotifyEngine = spotifyEngine;
        this.songRepository = songRepository;
        this.albumRepository = albumRepository;
        this.playlistRepository = playlistRepository;
        this.artistRepository = artistRepository;
        this.songArtistRepository = songArtistRepository;
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
                Artist artist = parseAndSaveArtist(accessToken, artistNode.path("id").asText());
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
            return albumRepository.save(album);

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

    // Used by parseSongFromJson to save the artist if not already in the database.
    private Artist parseAndSaveArtist(String accessToken, String spotifyArtistId) {
        return artistRepository.findBySpotifyArtistId(spotifyArtistId).orElseGet(() -> {
            Artist artist = new Artist(spotifyArtistId, "Unknown", "None");
            return artistRepository.save(artist);
        });
    }
}
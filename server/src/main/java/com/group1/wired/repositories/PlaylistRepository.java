package com.group1.wired.repositories;

import com.group1.wired.entities.Playlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PlaylistRepository extends JpaRepository<Playlist,Long> {
	
    Optional<Playlist> findBySpotifyPlaylistId(String spotifyPlaylistId);
	
}

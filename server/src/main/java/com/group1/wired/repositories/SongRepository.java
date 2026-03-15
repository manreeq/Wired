package com.group1.wired.repositories;

import com.group1.wired.entities.Song;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SongRepository extends JpaRepository<Song, Long> {
	
    Optional<Song> findBySpotifyTrackId(String spotifyTrackId);

}
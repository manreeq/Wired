package com.group1.wired.repositories;

import com.group1.wired.entities.Artist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ArtistRepository extends JpaRepository<Artist,Long> {
	
    Optional<Artist> findBySpotifyArtistId(String spotifyArtistId);
	
}

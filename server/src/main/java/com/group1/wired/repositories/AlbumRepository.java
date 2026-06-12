package com.group1.wired.repositories;

import com.group1.wired.entities.Album;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AlbumRepository extends JpaRepository<Album,Long> {
	
    Optional<Album> findBySpotifyAlbumId(String spotifyAlbumId);	

}

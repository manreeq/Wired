package com.group1.wired.repositories;

import com.group1.wired.entities.Song;
import com.group1.wired.entities.SongArtist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SongArtistRepository extends JpaRepository<SongArtist, Long> {

    // get all artists linked to a song
    List<SongArtist> findBySong(Song song);
}
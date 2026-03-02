package com.group1.wired.repositories;

import com.group1.wired.entities.AlbumSong;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AlbumSongRepository extends JpaRepository<AlbumSong,Long> {
}

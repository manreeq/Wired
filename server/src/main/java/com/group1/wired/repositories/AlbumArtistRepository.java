package com.group1.wired.repositories;

import com.group1.wired.entities.AlbumArtist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AlbumArtistRepository extends JpaRepository<AlbumArtist,Long> {
}

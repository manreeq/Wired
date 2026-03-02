package com.group1.wired.repositories;

import com.group1.wired.entities.SongPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SongPostRepository extends JpaRepository<SongPost,Long> {
}

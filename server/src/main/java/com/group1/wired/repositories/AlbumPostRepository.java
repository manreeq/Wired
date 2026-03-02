package com.group1.wired.repositories;

import com.group1.wired.entities.AlbumPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AlbumPostRepository extends JpaRepository<AlbumPost,Long> {
}

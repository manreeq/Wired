package com.group1.wired.repositories;

import com.group1.wired.entities.PlaylistPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlaylistPostRepository extends JpaRepository<PlaylistPost,Long> {
}

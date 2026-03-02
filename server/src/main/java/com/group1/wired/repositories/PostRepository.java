package com.group1.wired.repositories;

import com.group1.wired.entities.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<PostRepository, Long> {

}
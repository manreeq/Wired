package com.group1.wired.repositories;

import com.group1.wired.entities.ListeningActivityPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ListeningActivityPostRepository extends JpaRepository<ListeningActivityPost,Long> {
}

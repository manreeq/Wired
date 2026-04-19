package com.group1.wired.repositories;

import com.group1.wired.entities.Post;
import com.group1.wired.entities.Reaction;
import com.group1.wired.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReactionRepository extends JpaRepository<Reaction, Long> {

    Optional<Reaction> findByPostAndUser(Post post, User user);
    
}
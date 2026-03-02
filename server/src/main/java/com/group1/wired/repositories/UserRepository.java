package com.group1.wired.repositories;

import com.group1.wired.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {
	Optional<User> findBySpotifyURI(String spotifyURI);
}

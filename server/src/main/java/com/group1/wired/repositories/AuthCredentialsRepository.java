package com.group1.wired.repository;

import com.group1.wired.entities.AuthCredentials;
import com.group1.wired.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AuthCredentialsRepository extends JpaRepository<AuthCredentials, Long> {
    
    // Custom query method required by AuthService to find credentials by the associated User entity
    Optional<AuthCredentials> findByUser(User user);
}
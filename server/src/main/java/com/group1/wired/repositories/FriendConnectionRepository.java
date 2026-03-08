package com.group1.wired.repositories;

import com.group1.wired.entities.FriendConnection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FriendConnectionRepository extends JpaRepository<FriendConnection,Long> {
}

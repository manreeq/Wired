package com.group1.wired.repositories;

import com.group1.wired.entities.FriendConnection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.group1.wired.entities.User;
import java.util.List;
import java.util.Optional;

@Repository
public interface FriendConnectionRepository extends JpaRepository<FriendConnection,Long> {
	 List<FriendConnection> findByTargetUserAndStatus(User targetUser, String status);					//pending friend requests
	 List<FriendConnection> findByRequesterUserAndStatus(User requesterUser, String status);			//sent friend requests
	 Optional<FriendConnection> findByRequesterUserAndTargetUser(User requesterUser, User targetUser);	//are 2 users friends
}

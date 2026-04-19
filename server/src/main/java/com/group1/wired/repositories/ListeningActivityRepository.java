package com.group1.wired.repositories;

import com.group1.wired.entities.ListeningActivity;
import com.group1.wired.entities.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ListeningActivityRepository extends JpaRepository<ListeningActivity,Long> {
	
	@Query("""
	        SELECT la.song.songId, la.song.songName, la.song.albumArtUrl, COUNT(la) as listenCount
	        FROM ListeningActivity la
	        WHERE la.user = :user
	        GROUP BY la.song.songId, la.song.songName, la.song.albumArtUrl
	        ORDER BY listenCount DESC
	        LIMIT 5
	    """)
	    List<Object[]> findTop5SongsByUser(@Param("user") User user);	
}

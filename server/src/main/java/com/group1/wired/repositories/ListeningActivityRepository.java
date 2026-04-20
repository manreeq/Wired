package com.group1.wired.repositories;

import com.group1.wired.entities.ListeningActivity;
import com.group1.wired.entities.User;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ListeningActivityRepository extends JpaRepository<ListeningActivity,Long> {

	// Listening history for a user ordered by most recent, supports pagination for 5 / 10 / All
	List<ListeningActivity> findByUser_UserIDOrderByTimestampDesc(Long userId, Pageable pageable);

	// Unlimited version for "All"
	List<ListeningActivity> findByUser_UserIDOrderByTimestampDesc(Long userId);

	//top songs
	@Query("""
	        SELECT la.song.songId, la.song.songName, la.song.albumArtUrl, COUNT(la) as listenCount
	        FROM ListeningActivity la
	        WHERE la.user = :user
	        GROUP BY la.song.songId, la.song.songName, la.song.albumArtUrl
	        ORDER BY listenCount DESC
	        LIMIT 5
	    """)
	    List<Object[]> findTop5SongsByUser(@Param("user") User user);	
	//top artists, joins la on song on song artist on artist   
	@Query("""
    	    SELECT sa.artist.artistId, sa.artist.artistName, sa.artist.profilePictureUrl, COUNT(la) as listenCount
    	    FROM ListeningActivity la
    	    JOIN SongArtist sa ON sa.song = la.song
    	    WHERE la.user = :user
    	    GROUP BY sa.artist.artistId, sa.artist.artistName, sa.artist.profilePictureUrl
    	    ORDER BY listenCount DESC
    	    LIMIT 5
    	""")
    	List<Object[]> findTop5ArtistsByUser(@Param("user") User user);
    	
   //total listening time
    	
	@Query("""
			SELECT SUM(la.song.durationMs)
    	    FROM ListeningActivity la
    	    WHERE la.user = :user
    	""")
    	Long getTotalListeningTimeMs(@Param("user") User user);
			
	//top albums
	@Query("""
	    SELECT la.song.album.albumId, la.song.album.albumName, la.song.album.albumArtUrl, COUNT(la) as listenCount
	    FROM ListeningActivity la
	    WHERE la.user = :user
	    GROUP BY la.song.album.albumId, la.song.album.albumName, la.song.album.albumArtUrl
	    ORDER BY listenCount DESC
	    LIMIT 5
	""")
	List<Object[]> findTop5AlbumsByUser(@Param("user") User user);
			
		
}

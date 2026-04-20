package com.group1.wired.service;
import com.group1.wired.dto.TopArtistDTO;
import com.group1.wired.dto.TopSongDTO;
import com.group1.wired.dto.TopAlbumDTO;
import com.group1.wired.entities.Song;
import com.group1.wired.entities.SongArtist;
import com.group1.wired.entities.User;
import com.group1.wired.repositories.ListeningActivityRepository;
import com.group1.wired.repositories.SongArtistRepository;
import com.group1.wired.repositories.SongRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;


@Service
public class StatsService {

    private final ListeningActivityRepository listeningActivityRepository;
    private final SongArtistRepository songArtistRepository;
    private final SongRepository songRepository;

    @Autowired
    public StatsService(ListeningActivityRepository listeningActivityRepository,
                        SongArtistRepository songArtistRepository,
                        SongRepository songRepository) {
        this.listeningActivityRepository = listeningActivityRepository;
        this.songArtistRepository = songArtistRepository;
        this.songRepository = songRepository;
    }
    
    private LocalDateTime getStartDate(String range) {
        return switch (range) {
            case "week"  -> LocalDateTime.now().minusWeeks(1);
            case "month" -> LocalDateTime.now().minusMonths(1);
            case "year"  -> LocalDateTime.now().minusYears(1);
            default      -> LocalDateTime.of(2000, 1, 1, 0, 0); // all time
        };
    }

    public List<TopSongDTO> getTopSongs(User user, String range) {
    	LocalDateTime since = getStartDate(range);
    	 //run the query, each Object[] has [songId, songName, albumArtUrl, listenCount]
        List<Object[]> results = listeningActivityRepository.findTop5SongsByUser(user, since);
       
        List<TopSongDTO> topSongs = new ArrayList<>();

        for (Object[] row : results) {
            Long songId = (Long) row[0];
            String songName = (String) row[1];
            String albumArtUrl = (String) row[2];
            Long listenCount = (Long) row[3];

            //fetch the actual Song object using the songId from the query result
            Song song = songRepository.findById(songId)
                    .orElse(null);

            //if song doesnt exist in db, skip it
            if (song == null) continue;

            //get all artists linked to this song
            List<SongArtist> songArtists = songArtistRepository.findBySong(song);

            //extract just the artist names into a plain list of strings
            List<String> artistNames = songArtists.stream()
                    .map(sa -> sa.getArtist().getArtistName())
                    .toList();

            topSongs.add(new TopSongDTO(songName, albumArtUrl, artistNames, listenCount));
        }

        return topSongs;
    }
    
    public List<TopArtistDTO> getTopArtists(User user,  String range) {
    	LocalDateTime since = getStartDate(range);
    	// run the query, each Object[] contains [artistId, artistName, profilePictureUrl, listenCount]
    	List<Object[]> results = listeningActivityRepository.findTop5ArtistsByUser(user, since);

        List<TopArtistDTO> topArtists = new ArrayList<>();

        for (Object[] row : results) {
            String artistName = (String) row[1];
            String profilePictureUrl = (String) row[2];
            Long listenCount = (Long) row[3];

            topArtists.add(new TopArtistDTO(artistName, profilePictureUrl, listenCount));
        }

        return topArtists;
    }
    public String getTotalListeningTime(User user, String range) {
    	 LocalDateTime since = getStartDate(range);
    	Long totalListeningTime = listeningActivityRepository.getTotalListeningTimeMs(user, since);
    	if (totalListeningTime == null) {
    		return "0 minutes";
    	}
    	
    	long totalMinutes = totalListeningTime/60000;
    	return "Minutes Listened: " +  totalMinutes ;
    }
    
    public List<TopAlbumDTO> getTopAlbums(User user, String range) {
    	LocalDateTime since = getStartDate(range);
        // run the query
        List<Object[]> results = listeningActivityRepository.findTop5AlbumsByUser(user, since);

        List<TopAlbumDTO> topAlbums = new ArrayList<>();

        for (Object[] row : results) {
            String albumName = (String) row[1];
            String albumArtUrl = (String) row[2];
            Long listenCount = (Long) row[3];

            topAlbums.add(new TopAlbumDTO(albumName, albumArtUrl, listenCount));
        }

        return topAlbums;
    }
}
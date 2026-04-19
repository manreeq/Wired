package com.group1.wired.service;

import com.group1.wired.dto.TopSongDTO;
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

    public List<TopSongDTO> getTopSongs(User user) {

        //run the query, each Object[] has [songId, songName, albumArtUrl, listenCount]
        List<Object[]> results = listeningActivityRepository.findTop5SongsByUser(user);

        List<TopSongDTO> topSongs = new ArrayList<>();

        for (Object[] row : results) {
            Long songId = (Long) row[0];
            String songName = (String) row[1];
            String albumArtUrl = (String) row[2];
            Long listenCount = (Long) row[3];

            //fetch the actual Song object using the songId from the query result
            Song song = songRepository.findById(songId)
                    .orElse(null);

            //if song doesnt exist in db for some reason, skip it
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
}
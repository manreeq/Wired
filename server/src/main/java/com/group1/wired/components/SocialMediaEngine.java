package com.group1.wired.components;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.group1.wired.entities.*;
import com.group1.wired.repositories.*;

@Component
public class SocialMediaEngine {
	
	//initialize repositories which contains functions or SQL queries 
	//final so if repo doesnt exist, it crashes on start instead of when used
	
	//post repositories
	private final SongPostRepository songPostRepo;
    private final AlbumPostRepository albumPostRepo;
    private final PlaylistPostRepository playlistPostRepo;
    private final ListeningActivityPostRepository listeningActivityPostRepo;

    
    //entity repositories
    private final UserRepository userRepo;

    private final SongRepository songRepo;
    private final AlbumRepository albumRepo;
    private final PlaylistRepository playlistRepo;
    private final ListeningActivityRepository listeningActivityRepo;

    


    
    //constructor injection
    public SocialMediaEngine(
            SongPostRepository songPostRepo,
            AlbumPostRepository albumPostRepo,
            PlaylistPostRepository playlistPostRepo,
            ListeningActivityPostRepository listeningActivityPostRepo,
            UserRepository userRepo,
            SongRepository songRepo,
            AlbumRepository albumRepo,
            PlaylistRepository playlistRepo,
            ListeningActivityRepository listeningActivityRepo) {
        
        this.songPostRepo = songPostRepo;
        this.albumPostRepo = albumPostRepo;
        this.playlistPostRepo = playlistPostRepo;
        this.listeningActivityPostRepo = listeningActivityPostRepo;
        this.userRepo = userRepo;
        this.songRepo = songRepo;
        this.albumRepo = albumRepo;
        this.playlistRepo = playlistRepo;
        this.listeningActivityRepo = listeningActivityRepo;
    }

    
    @Transactional
    public SongPost createSongPost(Long songId, String content, Long userId) {

    	//fetch user
        User user =  userRepo.findById(userId)
        		.orElseThrow(() -> new IllegalArgumentException("User not found"));
        
        //fetch song
        // TO-DO: if song is not found, we have to parse the song manually 
        Song song = songRepo.findById(songId)
                .orElseThrow(() -> new IllegalArgumentException("Song not found"));
        
        //no need to fetch content because its part of the superuser
        //create new post
        SongPost post = new SongPost(user, content, song);
        
        //save
        return songPostRepo.save(post);
    }
    
    @Transactional
    public AlbumPost createAlbumPost(Long albumId, String content, Long userId) {

    	//fetch user
        User user =  userRepo.findById(userId)
        		.orElseThrow(() -> new IllegalArgumentException("User not found"));
        
        //fetch album
        // TO-DO: if album is not found, we have to parse the song manually 
        Album album = albumRepo.findById(albumId)
                        .orElseThrow(() -> new IllegalArgumentException("Album not found"));
        
        //no need to fetch content because its part of the superuser
        //create new post
        AlbumPost post = new AlbumPost(user, content, album);
        
        //save
        return albumPostRepo.save(post);
    }
    
    
    @Transactional
    public PlaylistPost createPlaylistPost(Long playlistId, String content, Long userId) {

    	//fetch user
        User user =  userRepo.findById(userId)
        		.orElseThrow(() -> new IllegalArgumentException("User not found"));
        
        //fetch song
        // TO-DO: if playlist is not found, we have to parse the song manually 
        Playlist playlist = playlistRepo.findById(playlistId)
                .orElseThrow(() -> new IllegalArgumentException("Song not found"));
        
        //no need to fetch content because its part of the superuser
        //create new post
        PlaylistPost post = new PlaylistPost(user, content, playlist);
        
        //save
        return playlistPostRepo.save(post);
    }
}


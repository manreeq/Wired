package com.group1.wired.components;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;
import java.util.List;

import com.group1.wired.entities.*;
import com.group1.wired.repositories.*;
import com.group1.wired.dto.*;
import com.group1.wired.service.AuthService;
import com.group1.wired.service.ParseService;

@Component
public class SocialMediaEngine {
	
    // post repositories
    private final SongPostRepository songPostRepo;
    private final AlbumPostRepository albumPostRepo;
    private final PlaylistPostRepository playlistPostRepo;
    private final ListeningActivityPostRepository listeningActivityPostRepo;

    // entity repositories
    private final UserRepository userRepo;
    private final ListeningActivityRepository listeningActivityRepo;
    
    private final CommentRepository commentRepo;
    private final ReactionRepository reactionRepo;
    private final PostRepository postRepo;

    // services for Spotify parsing
    private final AuthService authService;
    private final ParseService parseService;

    // constructor injection
    public SocialMediaEngine(
            SongPostRepository songPostRepo,
            AlbumPostRepository albumPostRepo,
            PlaylistPostRepository playlistPostRepo,
            ListeningActivityPostRepository listeningActivityPostRepo,
            UserRepository userRepo,
            ListeningActivityRepository listeningActivityRepo,
            AuthService authService,
            ParseService parseService, CommentRepository commentRepo, 
            ReactionRepository reactionRepo,
            PostRepository postRepo) {
        this.songPostRepo = songPostRepo;
        this.albumPostRepo = albumPostRepo;
        this.playlistPostRepo = playlistPostRepo;
        this.listeningActivityPostRepo = listeningActivityPostRepo;
        this.userRepo = userRepo;
        this.listeningActivityRepo = listeningActivityRepo;
        this.authService = authService;
        this.parseService = parseService;
        
        this.commentRepo = commentRepo;
        this.reactionRepo = reactionRepo;
        this.postRepo = postRepo;
    }

    
    @Transactional
    public SongPost createSongPost(String spotifyId, String content, Long userId) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Resolve or fetch-and-save the Song from Spotify
        String token = authService.getValidAccessToken(user);
        Song song = parseService.parseAndSaveSong(token, spotifyId);

        SongPost post = new SongPost(user, content, song);
        return songPostRepo.save(post);
    }

    @Transactional
    public AlbumPost createAlbumPost(String spotifyId, String content, Long userId) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Resolve or fetch-and-save the Album from Spotify
        String token = authService.getValidAccessToken(user);
        Album album = parseService.parseAndSaveAlbum(token, spotifyId);

        AlbumPost post = new AlbumPost(user, content, album);
        return albumPostRepo.save(post);
    }

    @Transactional
    public PlaylistPost createPlaylistPost(String spotifyId, String content, Long userId) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Resolve or fetch-and-save the Playlist from Spotify
        String token = authService.getValidAccessToken(user);
        Playlist playlist = parseService.parseAndSavePlaylist(token, spotifyId);

        PlaylistPost post = new PlaylistPost(user, content, playlist);
        return playlistPostRepo.save(post);
    }
    
    @Transactional
    public CommentDTO addComment(Long postId, Long userId, String content) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        Post post = postRepo.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found"));

        Comment comment = new Comment();
        comment.setUser(user);
        comment.setPost(post);
        comment.setContent(content);
        
        Comment savedComment = commentRepo.save(comment);

        return new CommentDTO(
                savedComment.getCommentId(), post.getPostID(), user.getUserID(),
                user.getDisplayName(), user.getProfilePictureURL(), 
                savedComment.getContent(), savedComment.getTimestamp()
        );
    }

    @Transactional
    public ReactionDTO addReaction(Long postId, Long userId, String reactionType) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        Post post = postRepo.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found"));

        // Check if the user has already reacted to this post
        Optional<Reaction> existingReactionOpt = reactionRepo.findByPostAndUser(post, user);

        if (existingReactionOpt.isPresent()) {
            Reaction existing = existingReactionOpt.get();
            
            // User clicked same reaction again (Toggle Off)
            if (existing.getReactionType().equals(reactionType)) {
                reactionRepo.delete(existing);
                // Return a DTO with a "REMOVED" flag so the frontend knows to delete it from UI
                return new ReactionDTO(existing.getReactionId(), postId, userId, user.getDisplayName(), "REMOVED");
            } 
            // User clicked a different reaction (Change Reaction)
            else {
                existing.setReactionType(reactionType);
                Reaction saved = reactionRepo.save(existing);
                return new ReactionDTO(saved.getReactionId(), postId, userId, user.getDisplayName(), saved.getReactionType());
            }
        }

        // No existing reaction, create new
        Reaction reaction = new Reaction();
        reaction.setUser(user);
        reaction.setPost(post);
        reaction.setReactionType(reactionType);

        Reaction savedReaction = reactionRepo.save(reaction);

        return new ReactionDTO(
                savedReaction.getReactionId(), post.getPostID(), user.getUserID(),
                user.getDisplayName(), savedReaction.getReactionType()
        );
    }
    
    // Fetch existing comments
    public List<CommentDTO> getCommentsForPost(Long postId) {
        return commentRepo.findByPost_PostIDOrderByTimestampAsc(postId).stream()
                .map(c -> new CommentDTO(
                        c.getCommentId(), c.getPost().getPostID(), c.getUser().getUserID(),
                        c.getUser().getDisplayName(), c.getUser().getProfilePictureURL(),
                        c.getContent(), c.getTimestamp()
                )).toList();
    }

    // Fetch existing reactins
    public List<ReactionDTO> getReactionsForPost(Long postId) {
        return reactionRepo.findByPost_PostID(postId).stream()
                .map(r -> new ReactionDTO(
                        r.getReactionId(), r.getPost().getPostID(), r.getUser().getUserID(),
                        r.getUser().getDisplayName(), r.getReactionType()
                )).toList();
    }
}


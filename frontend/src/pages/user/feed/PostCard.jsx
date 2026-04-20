import { useState, useEffect } from 'react';
import styles from './PostCard.module.css';

function PostCard({ item, userId, apiUrl, formatTimestamp }) {
    const [showComments, setShowComments] = useState(false);
    const [commentText, setCommentText] = useState('');
    
    const [comments, setComments] = useState(item.comments || []);
    const [reactions, setReactions] = useState(item.reactions || []);

    const [artistName, setArtistName] = useState('');

    // Fetch existing comments and reactions when post loads
    useEffect(() => {
        // Fetch Reactions
        fetch(`${apiUrl}/api/posts/${item.postID}/reactions`)
            .then(res => res.json())
            .then(data => {
                if (Array.isArray(data)) setReactions(data);
            })
            .catch(err => console.error("Error fetching reactions:", err));

        // Fetch Comments
        fetch(`${apiUrl}/api/posts/${item.postID}/comments`)
            .then(res => res.json())
            .then(data => {
                if (Array.isArray(data)) setComments(data);
            })
            .catch(err => console.error("Error fetching comments:", err));

        // Fetch Artist Data (Only if it's a song or album)
        if (item.song) {
            fetch(`${apiUrl}/api/posts/songs/${item.song.songId}/artist`)
                .then(res => res.json())
                .then(data => setArtistName(data.artistName))
                .catch(err => console.error("Error fetching song artist:", err));
        } else if (item.album) {
            fetch(`${apiUrl}/api/posts/albums/${item.album.albumId}/artist`)
                .then(res => res.json())
                .then(data => setArtistName(data.artistName))
                .catch(err => console.error("Error fetching album artist:", err));
        }
    }, [item.postID, apiUrl]);
    
    const handleAddComment = () => {
        if (!commentText.trim()) return;

        const payload = {
            userId: userId,
            payload: commentText
        };

        fetch(`${apiUrl}/api/posts/${item.postID}/comments`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload)
        })
        .then(res => {
            if (!res.ok) throw new Error("Failed to post comment");
            return res.json();
        })
        .then(newCommentDto => {
            setComments(prev => [...prev, newCommentDto]);
            setCommentText(''); 
        })
        .catch(err => console.error(err));
    };

    const handleReaction = (reactionType) => {
        const payload = {
            userId: userId,
            payload: reactionType
        };

        fetch(`${apiUrl}/api/posts/${item.postID}/reactions`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload)
        })
        .then(res => {
            if (!res.ok) throw new Error("Failed to post reaction");
            return res.json();
        })
        .then(newReactionDto => {
            setReactions(prev => {
                
                const filtered = prev.filter(r => String(r.userId) !== String(userId));
                
                // Toggle Off (Backend removed it)
                if (newReactionDto.reactionType === 'REMOVED') {
                    return filtered; 
                }
                
                // New Reaction or Updated Reaction
                return [...filtered, newReactionDto];
            });
        })
        .catch(err => console.error(err));
    };

    // dynamically calculate current reaction counts
    const getReactionCounts = () => {
        const counts = { Like: 0, Fire: 0, Love: 0 };
        reactions.forEach(r => {
            if (counts[r.reactionType] !== undefined) {
                counts[r.reactionType]++;
            }
        });
        return counts;
    };

    const reactionCounts = getReactionCounts();

    // Dynamically build the actual Spotify URL for clickable links 
    const getSpotifyLink = () => {
        if (item.song) return `https://open.spotify.com/track/${item.song.spotifyTrackId}`;
        if (item.album) return `https://open.spotify.com/album/${item.album.spotifyAlbumId}`;
        if (item.playlist) return `https://open.spotify.com/playlist/${item.playlist.spotifyPlaylistId}`;
        return "#";
    };

    const spotifyLink = getSpotifyLink();
    
    return (
        <div className={styles.postCardContainer}>
            
            {/* Header: User & Timestamp */}
            <div className={styles.header}>
                <span className={styles.authorName}>{item.user?.displayName || "Unknown User"}</span>
                <span className={styles.timestamp}>{formatTimestamp(item.timestamp)}</span>
            </div>
            
            {/* Post Content */}
            <p className={styles.caption}>{item.caption}</p>
            {/* Dynamic Media Content */}
            <div className={styles.mediaBox}>
                
                {/* Cover Art */}
                {(item.song || item.album) && (
                    <img 
                        src={item.song?.albumArtUrl || item.album?.albumArtUrl} 
                        alt="Cover Art" 
                        className={styles.coverArt} 
                    />
                )}

                {/* Media Info */}
                <div className={styles.mediaInfo}>
                    <div className={styles.mediaType}>
                        {item.song ? 'Song' : item.album ? 'Album' : item.playlist ? 'Playlist' : 'Media'}
                    </div>
                    
                    {/* Clickable Title Link */}
                    <div className={styles.mediaTitle}>
                        <a 
                            href={spotifyLink} 
                            target="_blank" 
                            rel="noopener noreferrer" 
                            className={styles.spotifyLink}
                        >
                            {item.song?.songName || item.album?.albumName || item.playlist?.playlistName || "Unknown Title"}
                        </a>
                    </div>

                    {/* Artist Attribution */}
                    {(item.song || item.album) && (
                        <div className={styles.artistName}>
                            By {artistName || "Loading..."}
                        </div>
                    )}
                </div>
            </div>

             {/* Reactions Bar */}
            <div className={styles.reactionsBar}>
                <button className={styles.reactionBtn} onClick={() => handleReaction('Like')}>
                    👍 {reactionCounts.Like > 0 && <span style={{ marginLeft: '4px', fontWeight: 'bold' }}>{reactionCounts.Like}</span>}
                </button>
                <button className={styles.reactionBtn} onClick={() => handleReaction('Fire')}>
                    🔥 {reactionCounts.Fire > 0 && <span style={{ marginLeft: '4px', fontWeight: 'bold' }}>{reactionCounts.Fire}</span>}
                </button>
                <button className={styles.reactionBtn} onClick={() => handleReaction('Love')}>
                    ❤️ {reactionCounts.Love > 0 && <span style={{ marginLeft: '4px', fontWeight: 'bold' }}>{reactionCounts.Love}</span>}
                </button>
            </div>

            {/* Comments Toggle */}
            <button 
                className={styles.toggleBtn}
                onClick={() => setShowComments(!showComments)}
            >
                {showComments ? "Hide Comments" : `View Comments (${comments.length})`}
            </button>

            {/* Comments Section */}
            {showComments && (
                <div className={styles.commentsSection}>
                    {/* List of existing comments */}
                    {comments.map((c, idx) => (
                        <div key={c.commentId || idx} className={styles.commentItem}>
                            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'baseline' }}>
                                <strong>{c.displayName}</strong>
                                <span style={{ fontSize: '0.75em', color: '#888' }}>
                                    {c.timestamp ? formatTimestamp(c.timestamp) : 'Just now'}
                                </span>
                            </div>
                            <p style={{ margin: '4px 0 0 0' }}>{c.content}</p>
                        </div>
                    ))}

                    {/* Add Comment Input */}
                    <div className={styles.commentInputArea}>
                        <input 
                            className={styles.commentInput}
                            type="text" 
                            placeholder="Add a comment..." 
                            value={commentText}
                            onChange={(e) => setCommentText(e.target.value)}
                        />
                        <button className={styles.postCommentBtn} onClick={handleAddComment}>
                            Post
                        </button>
                    </div>
                </div>
            )}
        </div>
    );
}

export default PostCard;
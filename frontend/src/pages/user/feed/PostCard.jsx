import { useState } from 'react';
import styles from './PostCard.module.css';

function PostCard({ item, userId, apiUrl, formatTimestamp }) {
    const [showComments, setShowComments] = useState(false);
    const [commentText, setCommentText] = useState('');
    
    const [comments, setComments] = useState(item.comments || []);
    const [reactions, setReactions] = useState(item.reactions || []);

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
            setReactions(prev => [...prev, newReactionDto]);
        })
        .catch(err => console.error(err));
    };

    return (
        <div className={styles.postCardContainer}>
            
            {/* Header: User & Timestamp */}
            <div className={styles.header}>
                <span className={styles.authorName}>{item.user?.displayName || "Unknown User"}</span>
                <span className={styles.timestamp}>{formatTimestamp(item.timestamp)}</span>
            </div>
            
            {/* Post Content */}
            <p className={styles.caption}>{item.caption}</p>
            <div className={styles.mediaBox}>
                🎧 <strong>Attached Media: </strong>
                {item.song?.songName || item.album?.albumName || item.playlist?.playlistName || "Unknown"}
            </div>

            {/* Reactions Bar */}
            <div className={styles.reactionsBar}>
                <button className={styles.reactionBtn} onClick={() => handleReaction('Like')}>👍</button>
                <button className={styles.reactionBtn} onClick={() => handleReaction('Fire')}>🔥</button>
                <button className={styles.reactionBtn} onClick={() => handleReaction('Love')}>❤️</button>
                <span className={styles.reactionCount}>
                    {reactions.length} reactions
                </span>
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
                            <strong>{c.displayName}: </strong> {c.content}
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
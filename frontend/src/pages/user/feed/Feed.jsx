import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';
import Navbar from '../../../components/navbar';
import styles from './Feed.module.css';

function Feed() {
    const navigate = useNavigate();
    const [showModal, setShowModal] = useState(false);

    const apiUrl = import.meta.env.VITE_API_BASE_URL || 'http://127.0.0.1:8080';
    
    // Form state variables
    const [postType, setPostType] = useState('song');
    const [mediaId, setMediaId] = useState('');
    const [content, setContent] = useState('');

    // One unified array for both Live Activities and Manual Posts
    const [feedItems, setFeedItems] = useState([]);

    // EFFECT 1: Fetch Database Posts (Historical)
    useEffect(() => {
        fetch(`${apiUrl}/api/posts/feed`)
            .then(response => {
                if (!response.ok) throw new Error('Failed to fetch feed history');
                return response.json();
            })
            .then(data => {
                // Inject a 'feedType' flag so our UI knows this is a manual post
                const formattedPosts = data.map(post => ({ ...post, feedType: 'MANUAL_POST' }));
                
                // Use a functional state update to safely merge with any live data 
                setFeedItems(prev => [...prev, ...formattedPosts]);
            })
            .catch(error => console.error("Error loading feed history:", error));
    }, []);

    // EFFECT 2: WebSocket Connection (Live)
    useEffect(() => {
        const wsUrl = import.meta.env.VITE_WS_URL || 'http://localhost:8080/chat';
        
        const client = new Client({
            webSocketFactory: () => new SockJS(wsUrl),
            onConnect: () => {
                client.subscribe('/topic/feed', (message) => {
                    const rawActivity = JSON.parse(message.body);
                    
                    // Inject a 'feedType' flag so our UI knows this is live
                    const activity = { ...rawActivity, feedType: 'LIVE_ACTIVITY' };

                    setFeedItems((prev) => {
                        // Filter out the older *live* post from this same user
                        const filtered = prev.filter(item => 
                            !(item.feedType === 'LIVE_ACTIVITY' && item.userId === activity.userId)
                        );
                        // Prepend the new live activity to the top
                        return [activity, ...filtered];
                    });
                });
            },
            onStompError: (frame) => console.error('STOMP error:', frame),
        });

        client.activate();
        return () => client.deactivate();
    }, []);

    // Transforms the raw backend timestamp into a human-readable format
    const formatTimestamp = (rawTime) => {
        if (!rawTime) return '';
        const date = new Date(rawTime);
        return date.toLocaleDateString(undefined, { 
            month: 'short', 
            day: 'numeric', 
            hour: '2-digit', 
            minute: '2-digit' 
        }); // e.g., "Apr 14, 5:30 PM"
    };


    // POST CREATION HANDLER
    const handlePostSubmit = () => {
        // STEP 1: Prove the button is actually attached
        alert("1. Button clicked! Starting function..."); 
        
        const storedUser = JSON.parse(localStorage.getItem('wiredUser')) || {};
        const userId = storedUser.id; 

        if (!userId) {
            alert("FAILED: Could not find user ID in localStorage.");
            return;
        }

        const payload = {
            mediaId: parseInt(mediaId, 10),
            content: content,
            userId: parseInt(userId, 10)
        };

        // STEP 2: Prove the data is formatted correctly
        alert("2. Data ready to send: " + JSON.stringify(payload)); 

        const postUrl = `${apiUrl}/api/posts/${postType}`;
        
        fetch(postUrl, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload)
        })
        .then(async res => {
            // STEP 3: Catch any backend crashes (like 500 or 404 errors)
            if (!res.ok) {
                const text = await res.text();
                alert(`FAILED: Server returned Error ${res.status}. Check console for details.`);
                throw new Error(`Server Error ${res.status}: ${text}`);
            }
            return res.json();
        })
        .then(data => {
            // STEP 4: Ultimate victory
            alert("3. SUCCESS! Database saved the post.");
            const newPost = { ...data, feedType: 'MANUAL_POST' };
            setFeedItems([newPost, ...feedItems]);
            setContent('');
            setMediaId('');
            setShowModal(false);
        })
        .catch(err => {
            // This catches network drops or CORS issues
            console.error("POST CRASHED:", err);
            alert("FAILED: The fetch request crashed completely. Open your browser console!");
        });
    };

    return (
        <div className={styles.container}>
            <Navbar />
            <div className={styles.body}>
                <div className={styles.sidebar}>
                    <button onClick={() => navigate('/profile')}>Profile</button>
                    <button>Friends</button>
                    <button onClick={() => setShowModal(true)}>Create Post</button>
                </div>

                <div className={styles.feed}>
                    <h2>Feed</h2>

                    {feedItems.length === 0 ? (
                        <p>Loading feed...</p>
                    ) : (
                        <div className={styles.postList} style={{ display: 'flex', flexDirection: 'column', gap: '15px' }}>
                            {feedItems.map((item, index) => {
                                
                                // RENDER LIVE ACTIVITY CARD
                                if (item.feedType === 'LIVE_ACTIVITY') {
                                    return (
                                        <div key={`live-${index}`} className={styles.postCard} style={{ border: '2px solid #1DB954', padding: '10px', borderRadius: '8px' }}>
                                            <div style={{ display: 'flex', alignItems: 'center', gap: '10px' }}>
                                                <img src={item.albumArtUrl} alt={item.songTitle} style={{ width: '50px', height: '50px' }} />
                                                <div>
                                                    <strong>{item.displayName} {item.isPlaying ? 'is now listening to' : 'was listening to'}</strong>
                                                    <p style={{ margin: 0 }}>{item.songTitle}</p>
                                                </div>
                                            </div>
                                        </div>
                                    );
                                }

                                // RENDER MANUAL POST CARD
                                if (item.feedType === 'MANUAL_POST') {
                                    return (
                                        <div key={`post-${item.postId || index}`} className={styles.postCard} style={{ border: '1px solid #ccc', padding: '15px', borderRadius: '8px' }}>
                                            
                                            {/* UPDATED: Name and Timestamp Header */}
                                            <div style={{ marginBottom: '10px', display: 'flex', flexDirection: 'column' }}>
                                                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                                                    <strong style={{ fontSize: '1.1em' }}>{item.user?.displayName || "Unknown User"}</strong>
                                                    <span style={{ fontSize: '0.85em', color: '#888' }}>
                                                        {formatTimestamp(item.timestamp)}
                                                    </span>
                                                </div>
                                                <p style={{ margin: '8px 0' }}>{item.caption}</p>
                                            </div>
                                            
                                            <div style={{ padding: '10px', backgroundColor: '#f9f9f9', borderRadius: '5px' }}>
                                                🎧 <strong>Attached Media: </strong> 
                                                {item.song?.songName || item.album?.albumName || item.playlist?.playlistName || "Unknown"}
                                            </div>
                                        </div>
                                    );
                                }
                                return null;
                            })}
                        </div>
                    )}
                </div>
            </div>

            {/* Create Post Modal */}
            {showModal && (
                <div className={styles.modalOverlay} onClick={() => setShowModal(false)}>
                    <div className={styles.modal} onClick={e => e.stopPropagation()}>
                        <h2>Create Post</h2>
                        <select value={postType} onChange={(e) => setPostType(e.target.value)} style={{ marginBottom: '10px' }}>
                            <option value="song">Song</option>
                            <option value="album">Album</option>
                            <option value="playlist">Playlist</option>
                        </select>
                        <input type="number" placeholder="Media ID" value={mediaId} onChange={(e) => setMediaId(e.target.value)} style={{ width: '100%', marginBottom: '10px' }}/>
                        <textarea placeholder="What are you listening to?" rows={4} value={content} onChange={(e) => setContent(e.target.value)} style={{ width: '100%', marginBottom: '10px' }} />
                        <div className={styles.modalButtons}>
                            <button onClick={() => setShowModal(false)}>Cancel</button>
                            <button onClick={handlePostSubmit}>Post</button>
                        </div>
                    </div>
                </div>
            )}
        </div>
    );
}

export default Feed;
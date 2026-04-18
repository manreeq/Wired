import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';
import Navbar from '../../../components/navbar';
import styles from './Feed.module.css';

function Feed() {
    const navigate = useNavigate();
    const [showModal, setShowModal] = useState(false);

    //entity of logged in user
    const storedUser = JSON.parse(localStorage.getItem('wiredUser')) || {};
    const userId = storedUser.id;
    const friendCode = storedUser.friendCode;

    // for adding friends modal
    const [showFriendsModal, setShowFriendsModal] = useState(false);

    // for friend requests modal
    const [showRequestsModal, setShowRequestsModal] = useState(false);

    const apiUrl = import.meta.env.VITE_API_BASE_URL || 'http://127.0.0.1:8080';

    // Form state variables
    const [postType, setPostType] = useState('song');
    const [mediaId, setMediaId] = useState('');
    const [content, setContent] = useState('');
    const [targetFriendCode,setTargetFriendCode] = useState('');

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

        if (!userId) return alert("Error: User ID not found.");

        const payload = {
            mediaId: parseInt(mediaId, 10),
            content: content,
            userId: parseInt(userId, 10)
        };

        fetch(`${apiUrl}/api/posts/${postType}`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload)
        })
        .then(res => res.json())
        .then(data => {
            const newPost = { ...data, feedType: 'MANUAL_POST' };
            setFeedItems([newPost, ...feedItems]);

            setContent('');
            setMediaId('');
            setShowModal(false);
        })
        .catch(err => console.error(err));
    };

    // FRIEND REQUEST HANDLER
    const handleFriendRequestSubmit = () => {
        if (!userId) return alert("Error: User ID not found.");
        const payload = {
          "requesterUserId": userId,
          "targetFriendCode": targetFriendCode,
          "status": "Accepted"
        }

        fetch(`${apiUrl}/api/friends/add`, {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify(payload)
                })

        setShowFriendsModal(false)
    }

    return (
        <div className={styles.container}>
            <Navbar />
            <div className={styles.body}>
                <div className={styles.sidebar}>
                    <button onClick={() => navigate('/profile')}>Profile</button>
                    <button onClick={() => setShowRequestsModal(true)}>Friend Requests</button>
                    <button onClick={() => setShowFriendsModal(true)}>Add Friend</button>
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

            {/*  show friend modal */}
            {showFriendsModal && (
                <div className={styles.modalOverlay} onClick={() => setShowFriendsModal(false)}>
                    <div className={styles.modal} onClick={e => e.stopPropagation()}>
                        <h2>Add Friend</h2>
                        <h3>Your friend code: {friendCode}</h3>
                        <textarea placeholder="Enter Friend Code" rows={1} value={targetFriendCode} onChange={(e) => setTargetFriendCode(e.target.value)} style={{ width: '100%', marginBottom: '10px' }} />
                        <div className={styles.modalButtons}>
                            <button onClick={() => setShowFriendsModal(false)}>Cancel</button>
                            <button onClick={handleFriendRequestSubmit}>Add</button>
                        </div>
                    </div>
                </div>
            )}

            {/* show requests modal */}
            {showRequestsModal && (
                <div className={styles.modalOverlay} onClick={() => setShowRequestsModal(false)}>
                    <div className={styles.modal} onClick={e => e.stopPropagation()}>
                        <h2>Friend Requests</h2>
                        <h3>Your friend code: {friendCode}</h3>
                        <textarea placeholder="THIS IS THE OTHER THING" rows={1} value={targetFriendCode} onChange={(e) => setTargetFriendCode(e.target.value)} style={{ width: '100%', marginBottom: '10px' }} />
                        <div className={styles.modalButtons}>
                            <button onClick={() => setShowRequestsModal(false)}>Cancel</button>
                            <button onClick={handleFriendRequestSubmit}>Add</button>
                        </div>
                    </div>
                </div>
            )}
        </div>
    );
}

export default Feed;
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

    // for the friend list
    const [friendList, setFriendList] = useState([]);

    // for the friend requests modal
    const [friendRequests, setFriendRequests] = useState([]);

    const apiUrl = import.meta.env.VITE_API_BASE_URL || 'http://127.0.0.1:8080';

    // Form state variables
    const [postType, setPostType] = useState('song');
    const [mediaId, setMediaId] = useState('');
    const [content, setContent] = useState('');
    const [targetFriendCode,setTargetFriendCode] = useState('');

    // Separate state arrays — keeps live and manual posts completely decoupled
    const [liveActivities, setLiveActivities] = useState([]);
    const [manualPosts, setManualPosts] = useState([]);

    // Wait for both fetches to complete before deciding if empty
    const [isLoading, setIsLoading] = useState(true);

    // EFFECT 1: Fetch initial live state (songs already playing before we loaded the page)
    useEffect(() => {
        fetch(`${apiUrl}/api/feed/live`)
            .then(response => {
                if (!response.ok) throw new Error('Failed to fetch live feed');
                return response.json();
            })
            .then(data => {
                setLiveActivities(data);
            })
            .catch(error => console.error("Error loading live feed:", error))
            .finally(() => setIsLoading(false));
    }, []);

    // EFFECT 2: Fetch historical manual posts
    useEffect(() => {
        fetch(`${apiUrl}/api/posts/feed`)
            .then(response => {
                if (!response.ok) throw new Error('Failed to fetch feed history');
                return response.json();
            })
            .then(data => {
                setManualPosts(data);
            })
            .catch(error => console.error("Error loading feed history:", error));
    }, []);

    // EFFECT 3: WebSocket Connection — liveActivities
    useEffect(() => {
        const wsUrl = import.meta.env.VITE_WS_URL || 'http://localhost:8080/chat';

        const client = new Client({
            webSocketFactory: () => new SockJS(wsUrl),
            onConnect: () => {
                client.subscribe('/topic/feed', (message) => {
                    const activity = JSON.parse(message.body);

                    setLiveActivities(prev => {
                        // Replace the older live entry for this user, or prepend if new
                        const filtered = prev.filter(item => item.userId !== activity.userId);
                        return [activity, ...filtered];
                    });
                });
            },
            onStompError: (frame) => console.error('STOMP error:', frame),
        });

        client.activate();
        return () => client.deactivate();
    }, []);

    useEffect(() => {
      fetchFriendList();
    }, [userId]);

    useEffect(() => {
      fetchPendingRequests();
    }, [userId]);


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
            spotifyUrl: mediaId,
            content: content,
            userId: parseInt(userId, 10)
        };

        fetch(`${apiUrl}/api/posts/${postType}`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload)
        })
            .then(res => {
              if (!res.ok) {
                  throw new Error(`Failed to create post. Server returned status ${res.status}`);
              }
              return res.json();
            })
            .then(data => {
                // Prepend to manualPosts
                setManualPosts(prev => [data, ...prev]);

                setContent('');
                setMediaId('');
                setShowModal(false);
            })
            .catch(err => console.error(err));
    };
  
    const hasContent = liveActivities.length > 0 || manualPosts.length > 0;

    // FRIEND REQUEST HANDLER
    const handleFriendRequestSubmit = () => {
        if (!userId) return alert("Error: User ID not found.");
        const payload = {
          "requesterUserId": userId,
          "targetFriendCode": targetFriendCode,
          "status": "Pending"
        }

        fetch(`${apiUrl}/api/friends/add`, {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify(payload)
        })
        .then(data => {
            setTargetFriendCode('');
            setShowFriendsModal(false);
        })

    }

    // Fetch friend list
    const fetchFriendList = () => {
      if (!userId) return alert("Error: User ID not found.");

      fetch(`${apiUrl}/api/friends/list/${userId}`)
        .then(response => {
          if (!response.ok) throw new Error("Failed to fetch friend list");
          return response.json();
        })
        .then(data => {
          // data is an array of FriendListDTO objects
          setFriendList(data);
        })
        .catch(error => console.error("Error loading friend list:", error));
    };

    // Fetch pending friend requests
    const fetchPendingRequests = () => {
          if (!userId) return alert("Error: User ID not found.");

          fetch(`${apiUrl}/api/friends/requests/${userId}`)
            .then(response => {
              if (!response.ok) throw new Error("Failed to pending friend requests");
              return response.json();
            })
            .then(data => {
              // data is an array of PendingRequestsDTO objects
              setFriendRequests(data);
            })
            .catch(error => console.error("Error loading pending friend requests:", error));
    };

    // accept friend request handler
    const handleFriendRequestAccept = (connectionId) => {
      fetch(`${apiUrl}/api/friends/requests/accept/${connectionId}`, { method: "PUT" })
        .then(() => fetchPendingRequests());
    };

    return (
        <div className={styles.container}>
            <Navbar />
            <div className={styles.body}>
                <div className={styles.sidebar}>
                    <button onClick={() => navigate('/profile')}>Profile</button>
                    <button onClick={() => setShowRequestsModal(true)}>Friend Requests</button>
                    <button onClick={() => setShowFriendsModal(true)}>Add Friend</button>
                    <button onClick={() => setShowModal(true)}>Create Post</button>

                    <ul>
                      {friendList
                          .filter(friend => friend.status === "Accepted")
                          .map(friend => (
                        <li key={friend.connectionId}>
                          {friend.requesterDisplayName} ({friend.requesterFriendCode}) →
                          {friend.targetDisplayName} ({friend.targetFriendCode})
                          — Status: {friend.status}
                        </li>
                      ))}
                    </ul>
                </div>

                <div className={styles.feed}>
                    <h2>Feed</h2>

                    {isLoading ? (
                        <p>Loading feed...</p>
                    ) : !hasContent ? (
                        <p>No posts available yet.</p>
                    ) : (
                        <div className={styles.postList} style={{ display: 'flex', flexDirection: 'column', gap: '15px' }}>

                            {/* LIVE ACTIVITIES */}
                            {liveActivities.map((item) => (
                                <div key={`live-${item.userId}`} className={styles.postCard} style={{ border: '2px solid #1DB954', padding: '10px', borderRadius: '8px' }}>
                                    <div style={{ display: 'flex', alignItems: 'center', gap: '10px' }}>
                                        <img src={item.albumArtUrl} alt={item.songTitle} style={{ width: '50px', height: '50px' }} />
                                        <div>
                                            <strong>{item.displayName} {item.isPlaying ? 'is now listening to' : 'was listening to'}</strong>
                                            <p style={{ margin: 0 }}>{item.songTitle}</p>
                                        </div>
                                    </div>
                                </div>
                            ))}

                            {/* MANUAL POSTS */}
                            {manualPosts.map((item, index) => (
                                <div key={`post-${item.postID || index}`} className={styles.postCard} style={{ border: '1px solid #ccc', padding: '15px', borderRadius: '8px' }}>

                                    {/* Name and Timestamp Header */}
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
                            ))}

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
                        <input 
                            type="text" 
                            placeholder="Paste Spotify URL or ID" 
                            value={mediaId} 
                            onChange={(e) => {
                                const val = e.target.value;
                                setMediaId(val);
                                if (val.includes('/track/')) setPostType('song');
                                else if (val.includes('/album/')) setPostType('album');
                                else if (val.includes('/playlist/')) setPostType('playlist');
                            }} 
                            style={{ width: '100%', marginBottom: '10px' }} 
                        />
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
                        <ul>
                          {friendRequests
//                               .filter(friend => friend.status === "Pending")
                              .map(friend => (
                            <li key={friend.connectionId}>
                                {/* left */}
                                <div>
                                Requester: {friend.requesterDisplayName} ({friend.requesterFriendCode}) <br/>
                                Targer: {friend.targetDisplayName} ({friend.targetFriendCode}) <br/>
                                Status: {friend.status}
                                </div>
                                {/* right */}
                                <div>
                                    <button onClick={() => handleFriendRequestAccept(friend.connectionId)}>Accept</button>
                                    <button onClick={handleFriendRequestSubmit}>Reject</button>
                                </div>
                            </li>
                          ))}
                        </ul>
                    </div>
                </div>
            )}
        </div>
    );
}

export default Feed;
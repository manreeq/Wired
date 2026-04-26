import { useState, useEffect } from 'react';
import toast from 'react-hot-toast';
import { useNavigate } from 'react-router-dom';
import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';
import Navbar from '../../../components/navbar';
import styles from './Feed.module.css';
import PostCard from './PostCard';

// Helper to read the wiredUser cookie (same as navbar)
const getCookie = (name) => {
    const value = `; ${document.cookie}`;
    const parts = value.split(`; ${name}=`);
    if (parts.length === 2) {
        try { return JSON.parse(decodeURIComponent(parts.pop().split(';').shift())); }
        catch (e) { return {}; }
    }
    return {};
};

function Feed() {
    const navigate = useNavigate();
    const [showModal, setShowModal] = useState(false);

    const storedUser = getCookie('wiredUser');
    const userId = storedUser.userId || storedUser.id;
    const friendCode = storedUser.friendCode;
    const currentUserName = storedUser.name;
    const currentUserPic = storedUser.profilePicUrl;

    // Sidebar dropdown states
    const [showFriends, setShowFriends] = useState(false);
    const [showRequests, setShowRequests] = useState(false);

    // Remove-friend confirmation dialog state
    const [pendingRemoveId, setPendingRemoveId] = useState(null);

    // for the friend list
    const [friendList, setFriendList] = useState([]);

    // for the friend requests
    const [friendRequests, setFriendRequests] = useState([]);

    // add friend field inside requests dropdown
    const [targetFriendCode, setTargetFriendCode] = useState('');

    const apiUrl = import.meta.env.VITE_API_BASE_URL || 'http://127.0.0.1:8080';

    // Form state for post creation
    const [postType, setPostType] = useState('song');
    const [mediaId, setMediaId] = useState('');
    const [content, setContent] = useState('');

    // Separate state arrays — keeps live and manual posts completely decoupled
    const [liveActivities, setLiveActivities] = useState([]);
    const [manualPosts, setManualPosts] = useState([]);

    // Wait for both fetches to complete before deciding if empty
    const [isLoading, setIsLoading] = useState(true);

    // EFFECT 1: Fetch initial live state
    useEffect(() => {
        fetch(`${apiUrl}/api/feed/live`)
            .then(response => {
                if (!response.ok) throw new Error('Failed to fetch live feed');
                return response.json();
            })
            .then(data => setLiveActivities(data))
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
            .then(data => setManualPosts(data))
            .catch(error => console.error("Error loading feed history:", error));
    }, []);

    // EFFECT 3: WebSocket Connection
    useEffect(() => {
        const wsUrl = `${import.meta.env.VITE_API_BASE_URL}/chat`

        const client = new Client({
            webSocketFactory: () => new SockJS(wsUrl),
            onConnect: () => {
                client.subscribe('/topic/feed', (message) => {
                    const activity = JSON.parse(message.body);
                    setLiveActivities(prev => {
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

    useEffect(() => { fetchFriendList(); }, [userId]);
    useEffect(() => { fetchPendingRequests(); }, [userId]);

    // Transforms the raw backend timestamp into a human-readable format
    const formatTimestamp = (rawTime) => {
        if (!rawTime) return '';
        const date = new Date(rawTime);
        return date.toLocaleDateString(undefined, {
            month: 'short', day: 'numeric', hour: '2-digit', minute: '2-digit'
        });
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
                if (!res.ok) throw new Error(`Failed to create post. Server returned status ${res.status}`);
                return res.json();
            })
            .then(data => {
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
            requesterUserId: userId,
            targetFriendCode: targetFriendCode,
            status: "Pending"
        };
        fetch(`${apiUrl}/api/friends/add`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload)
        })
            .then(res => {
                if (!res.ok) throw new Error('Failed to send friend request');
                setTargetFriendCode('');
                toast.success('Friend request sent!');
            })
            .catch(err => {
                console.error(err);
                toast.error('Failed to send friend request.');
            });
    };

    const fetchFriendList = () => {
        if (!userId) return;
        fetch(`${apiUrl}/api/friends/list/${userId}`)
            .then(response => {
                if (!response.ok) throw new Error("Failed to fetch friend list");
                return response.json();
            })
            .then(data => setFriendList(data))
            .catch(error => console.error("Error loading friend list:", error));
    };

    const fetchPendingRequests = () => {
        if (!userId) return;
        fetch(`${apiUrl}/api/friends/requests/${userId}`)
            .then(response => {
                if (!response.ok) throw new Error("Failed to fetch pending friend requests");
                return response.json();
            })
            .then(data => setFriendRequests(data))
            .catch(error => console.error("Error loading pending friend requests:", error));
    };

    const handleFriendRequestAccept = (connectionId) => {
        // Grab the requester's name before removing the request from state
        const req = friendRequests.find(r => r.connectionId === connectionId);
        const [acceptedName] = req ? getFriendDisplay(
            friendCode,
            req.requesterDisplayName, req.requesterFriendCode, req.requesterId, null,
            req.targetDisplayName, req.targetFriendCode, req.targetId, null
        ) : ['Someone'];

        fetch(`${apiUrl}/api/friends/requests/accept/${connectionId}`, { method: "PUT" })
            .then(res => {
                if (!res.ok) throw new Error('Failed to accept request');
                return fetchPendingRequests();
            })
            .then(() => fetchFriendList())
            .then(() => toast.success(`You're now friends with ${acceptedName}!`))
            .catch(err => {
                console.error(err);
                toast.error('Failed to accept friend request.');
            });
    };

    const handleFriendRequestDecline = (connectionId) => {
        const req = friendRequests.find(r => r.connectionId === connectionId);
        const [declinedName] = req ? getFriendDisplay(
            friendCode,
            req.requesterDisplayName, req.requesterFriendCode, req.requesterId, null,
            req.targetDisplayName, req.targetFriendCode, req.targetId, null
        ) : ['Someone'];

        fetch(`${apiUrl}/api/friends/requests/decline/${connectionId}`, { method: "PUT" })
            .then(res => {
                if (!res.ok) throw new Error('Failed to decline request');
                return fetchPendingRequests();
            })
            .then(() => fetchFriendList())
            .then(() => toast.success(`You've declined ${declinedName}'s friend request.`))
            .catch(err => {
                console.error(err);
                toast.error('Failed to decline friend request.');
            });
    };

    const handleRemoveFriend = (connectionId) => {
        const entry = friendList.find(f => f.connectionId === connectionId);
        const [removedName] = entry ? getFriendDisplay(
            friendCode,
            entry.requesterDisplayName, entry.requesterFriendCode, entry.requesterId, null,
            entry.targetDisplayName, entry.targetFriendCode, entry.targetId, null
        ) : ['Friend'];

        fetch(`${apiUrl}/api/friends/remove/${connectionId}`, { method: "DELETE" })
            .then(res => {
                if (!res.ok) throw new Error('Failed to remove friend');
                return fetchPendingRequests();
            })
            .then(() => fetchFriendList())
            .then(() => toast.success(`${removedName} has been removed as a friend.`))
            .catch(err => {
                console.error(err);
                toast.error('Failed to remove friend.');
            });
        setPendingRemoveId(null);
    };

    function getFriendDisplay(userFriendCode,
        requesterName, requesterCode, requesterId, requesterPic,
        targetName, targetCode, targetId, targetPic) {
        if (userFriendCode === requesterCode) {
            return [targetName, targetCode, targetId, targetPic];
        } else {
            return [requesterName, requesterCode, requesterId, requesterPic];
        }
    }

    const allowedUserIds = new Set([
        Number(userId),
        ...friendList
            .filter(friend => friend.status === "Accepted")
            .map(friend => {
                if (friend.requesterId === Number(userId)) return Number(friend.targetId);
                return Number(friend.requesterId);
            })
    ]);

    console.log("Allowed IDs:", [...allowedUserIds]);

    return (
        <div className={styles.container}>
            <Navbar />
            <div className={styles.body}>

                {/* ========== SIDEBAR ========== */}
                <div className={styles.sidebar}>

                    {/* User identity card — clickable, navigates to profile */}
                    <div className={styles.sidebarProfile} onClick={() => navigate('/profile')}>
                        {currentUserPic && currentUserPic !== 'None' ? (
                            <img src={currentUserPic} alt={currentUserName} className={styles.sidebarAvatar} />
                        ) : (
                            <div className={styles.sidebarAvatarPlaceholder}>
                                {currentUserName ? currentUserName.charAt(0).toUpperCase() : '?'}
                            </div>
                        )}
                        <span className={styles.sidebarUsername}>{currentUserName || 'Profile'}</span>
                    </div>

                    {/* Friends dropdown */}
                    <button
                        className={styles.sidebarDropdownBtn}
                        onClick={() => setShowFriends(prev => !prev)}
                    >
                        <span>Friends</span>
                        <span className={styles.chevron}>{showFriends ? '▲' : '▼'}</span>
                    </button>

                    {showFriends && (
                        <div className={styles.dropdownList}>
                            <div className={styles.friendCodeRow}>
                                <span className={styles.friendCodeLabel}>Your code:</span>
                                <span className={styles.friendCode}>{friendCode}</span>
                            </div>
                            <ul className={styles.friendsList}>
                                {friendList
                                    .filter(friend => friend.status === "Accepted")
                                    .map(friend => {
                                        const [fName, fCode, fId, fPic] = getFriendDisplay(
                                            friendCode,
                                            friend.requesterDisplayName,
                                            friend.requesterFriendCode,
                                            friend.requesterId,
                                            friend.requesterProfilePicUrl,
                                            friend.targetDisplayName,
                                            friend.targetFriendCode,
                                            friend.targetId,
                                            friend.targetProfilePicUrl
                                        );
                                        return (
                                            <li key={friend.connectionId} className={styles.friendItem}>
                                                {/* Avatar */}
                                                {fPic && fPic !== 'None' ? (
                                                    <img src={fPic} alt={fName} className={styles.friendAvatar} />
                                                ) : (
                                                    <div className={styles.friendAvatarPlaceholder}>
                                                        {fName ? fName.charAt(0).toUpperCase() : '?'}
                                                    </div>
                                                )}
                                                {/* Info */}
                                                <a href={`/profile/${fId}`} className={styles.friendInfo}>
                                                    <span className={styles.friendName}>{fName}</span>
                                                    <span className={styles.friendCodeSmall}>({fCode})</span>
                                                </a>
                                                {/* Remove X button */}
                                                <button
                                                    className={styles.removeBtn}
                                                    onClick={() => setPendingRemoveId(friend.connectionId)}
                                                    title="Remove friend"
                                                >
                                                    ✕
                                                </button>
                                            </li>
                                        );
                                    })
                                }
                                {friendList.filter(f => f.status === "Accepted").length === 0 && (
                                    <li className={styles.emptyNote}>No friends yet.</li>
                                )}
                            </ul>
                        </div>
                    )}

                    {/* Friend Requests dropdown */}
                    <button
                        className={styles.sidebarDropdownBtn}
                        onClick={() => {
                            fetchPendingRequests();
                            setShowRequests(prev => !prev);
                        }}
                    >
                        <span>Friend Requests {friendRequests.length > 0 && `(${friendRequests.length})`}</span>
                        <span className={styles.chevron}>{showRequests ? '▲' : '▼'}</span>
                    </button>

                    {showRequests && (
                        <div className={styles.dropdownList}>
                            {/* Incoming requests list */}
                            {friendRequests.length === 0 ? (
                                <p className={styles.emptyNote}>No pending requests.</p>
                            ) : (
                                <ul className={styles.requestsList}>
                                    {friendRequests.map(req => {
                                        const [rName] = getFriendDisplay(
                                            friendCode,
                                            req.requesterDisplayName,
                                            req.requesterFriendCode,
                                            req.requesterId,
                                            null,
                                            req.targetDisplayName,
                                            req.targetFriendCode,
                                            req.targetId,
                                            null
                                        );
                                        return (
                                            <li key={req.connectionId} className={styles.requestItem}>
                                                <span className={styles.requestName}>{rName} wants to be friends</span>
                                                <div className={styles.requestActions}>
                                                    <button
                                                        className={styles.acceptBtn}
                                                        onClick={() => handleFriendRequestAccept(req.connectionId)}
                                                        title="Accept"
                                                    >✓</button>
                                                    <button
                                                        className={styles.declineBtn}
                                                        onClick={() => handleFriendRequestDecline(req.connectionId)}
                                                        title="Decline"
                                                    >✕</button>
                                                </div>
                                            </li>
                                        );
                                    })}
                                </ul>
                            )}

                            {/* Add friend field */}
                            <div className={styles.addFriendRow}>
                                <input
                                    className={styles.addFriendInput}
                                    type="text"
                                    placeholder="Enter friend code..."
                                    value={targetFriendCode}
                                    onChange={(e) => setTargetFriendCode(e.target.value)}
                                    onKeyDown={(e) => e.key === 'Enter' && handleFriendRequestSubmit()}
                                />
                                <button
                                    className={styles.addFriendSendBtn}
                                    onClick={handleFriendRequestSubmit}
                                    title="Send request"
                                >➤</button>
                            </div>
                        </div>
                    )}
                </div>

                {/* ========== FEED ========== */}
                <div className={styles.feed}>
                    <h2>Feed</h2>

                    {isLoading ? (
                        <p>Loading feed...</p>
                    ) : !hasContent ? (
                        <p>No posts available yet.</p>
                    ) : (
                        <div className={styles.postList} style={{ display: 'flex', flexDirection: 'column', gap: '15px' }}>

                            {/* LIVE ACTIVITIES */}
                            {liveActivities
                                .filter(item => allowedUserIds.has(item.userId))
                                .filter(item => !item.isHistoryPrivate || Number(item.userId) === Number(userId))
                                .map((item) => (
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
                            {manualPosts
                                .filter(item => allowedUserIds.has(item.user?.userID))
                                .map((item, index) => (
                                    <PostCard
                                        key={`post-${item.postID || index}`}
                                        item={item}
                                        userId={userId}
                                        apiUrl={apiUrl}
                                        formatTimestamp={formatTimestamp}
                                    />
                                ))}

                        </div>
                    )}
                </div>
            </div>

            {/* ========== FLOATING CREATE POST BUTTON ========== */}
            <button className={styles.fab} onClick={() => setShowModal(true)} title="Create Post">
                +
            </button>

            {/* Remove Friend Confirmation Dialog */}
            {pendingRemoveId && (
                <div className={styles.modalOverlay} onClick={() => setPendingRemoveId(null)}>
                    <div className={styles.modal} onClick={e => e.stopPropagation()}>
                        <h2>Remove Friend?</h2>
                        <p style={{ color: 'var(--wired-text-muted)' }}>Are you sure you want to remove this friend?</p>
                        <div className={styles.modalButtons}>
                            <button onClick={() => setPendingRemoveId(null)}>Cancel</button>
                            <button onClick={() => handleRemoveFriend(pendingRemoveId)} style={{ backgroundColor: 'var(--wired-error)', borderColor: 'var(--wired-error)', color: '#fff' }}>Remove</button>
                        </div>
                    </div>
                </div>
            )}

            {/* Create Post Modal */}
            {showModal && (
                <div className={styles.modalOverlay} onClick={() => setShowModal(false)}>
                    <div className={styles.modal} onClick={e => e.stopPropagation()}>
                        <h2>Create Post</h2>
                        <select value={postType} onChange={(e) => setPostType(e.target.value)}>
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
                        />
                        <textarea placeholder="What are you listening to?" rows={4} value={content} onChange={(e) => setContent(e.target.value)} />
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
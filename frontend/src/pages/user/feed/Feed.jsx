import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';
import Navbar from '../../../components/navbar';
import styles from './Feed.module.css';

function Feed() {
    const navigate = useNavigate();
    const [showModal, setShowModal] = useState(false);
    const [feedPosts, setFeedPosts] = useState([]);

    useEffect(() => {
        const client = new Client({
            webSocketFactory: () => new SockJS('http://localhost:8080/chat'),
            onConnect: () => {
                client.subscribe('/topic/feed', (message) => {
                    const activity = JSON.parse(message.body);
                    setFeedPosts((prev) => {
                        // Remove any older post from this same user
                        const filtered = prev.filter(post => post.userId !== activity.userId);
                        // Prepend the new updated activity at the top!
                        return [activity, ...filtered];
                    });
                });
            },
            onStompError: (frame) => {
                console.error('STOMP error:', frame);
            },
        });

        client.activate();

        // Cleanup on unmount
        return () => client.deactivate();
    }, []);

    return (
        <div className={styles.container}>

            <Navbar />

            <div className={styles.body}>

                {/* Sidebar */}
                <div className={styles.sidebar}>
                    <button onClick={() => navigate('/profile')}>Profile</button>
                    <button>Friends</button>
                    <button>Add Friends</button>
                    <button onClick={() => setShowModal(true)}>Create Post</button>
                </div>

                {/* Main feed area */}
                <div className={styles.feed}>
                    <h2>Feed</h2>

                    {feedPosts.length === 0 ? (
                        <p>Waiting for you or your friends to start listening to a new song on Spotify...</p>
                    ) : (
                        <div className={styles.postList}>
                            {feedPosts.map((post, index) => (
                                <div key={index} className={styles.postCard}>
                                    <img
                                        src={post.albumArtUrl}
                                        alt={post.songTitle}
                                    />
                                    <div className={styles.postInfo}>
                                        <strong>
                                            {post.displayName} {post.isPlaying ? 'is now listening to' : 'was listening to'}
                                        </strong>
                                        <span>{post.songTitle}</span>
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
                        <textarea placeholder="What are you listening to?" rows={4} />
                        <div className={styles.modalButtons}>
                            <button onClick={() => setShowModal(false)}>Cancel</button>
                            <button>Post</button>
                        </div>
                    </div>
                </div>
            )}

        </div>
    );
}

export default Feed;
import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import Navbar from '../../../components/Navbar';
import styles from './Feed.module.css';

function Feed() {
    const navigate = useNavigate();
    const [showModal, setShowModal] = useState(false);

    // variables to hold the form data
    const [postType, setPostType] = useState('song');
    const [mediaId, setMediaId] = useState('');
    const [content, setContent] = useState('');

    // state array to hold and display the feed of posts
    const [posts, setPosts] = useState([]);

    const handlePostSubmit = () => {
        // Pull globally stored user ID (from calback upon login)
        const storedUser = JSON.parse(localStorage.getItem('wiredUser')) || {};
        const userId = storedUser.id;

        if (!userId) {
            alert("Error: User ID not found. Please log in again.");
            return;
        }

        // Setup post payload
        const payload = {
            mediaId: parseInt(mediaId, 10), 
            content: content,
            userId: parseInt(userId, 10)
        };

        // Send request to dynamic endpoint based on dropdown selection
        fetch(`http://127.0.0.1:8080/api/posts/${postType}`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(payload)
        })
        .then(response => {
            if (!response.ok) {
                throw new Error('Failed to create post');
            }
            return response.json();
        })
        .then(data => {
            console.log("Post created successfully:", data);

            // Add new post (returned by springboot) to top of feed
            setPosts([data, ...posts]);
            
            // Reset form and close the modal upon success
            setContent('');
            setMediaId('');
            setPostType('song');
            setShowModal(false);
            alert("Post created successfully!");
        })
        .catch(error => {
            console.error("Error creating post:", error);
            alert("Failed to create post. Please make sure the Media ID exists in your database.");
        });
    };

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
                    
                    {/* Conditional rendering for the post feed */}
                    {posts.length === 0 ? (
                        <p>No posts yet.</p>
                    ) : (
                        <div style={{ display: 'flex', flexDirection: 'column', gap: '15px' }}>
                            {posts.map((post, index) => (
                                <div key={index} style={{ border: '1px solid #ccc', padding: '15px', borderRadius: '8px', backgroundColor: '#fff' }}>
                                    
                                    {/* Display User's Name and Caption */}
                                    <div style={{ marginBottom: '10px' }}>
                                        <strong style={{ fontSize: '1.1em' }}>
                                            {post.user?.displayName || "Unknown User"}
                                        </strong>
                                        <p style={{ margin: '5px 0' }}>{post.caption}</p>
                                    </div>
                                    
                                    {/* Display Attached Media Name dynamically */}
                                    <div style={{ padding: '10px', backgroundColor: '#f9f9f9', borderRadius: '5px', fontSize: '0.9em', color: '#555' }}>
                                        🎧 <strong>Listening to: </strong> 
                                        {
                                            post.song?.songName || 
                                            post.album?.albumName || 
                                            post.playlist?.playlistName || 
                                            "Unknown Media"
                                        }
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

                        {/* NEW: Dropdown for Post Type */}
                        <div style={{ marginBottom: '10px' }}>
                            <label style={{ marginRight: '10px' }}>Post Type: </label>
                            <select value={postType} onChange={(e) => setPostType(e.target.value)}>
                                <option value="song">Song</option>
                                <option value="album">Album</option>
                                <option value="playlist">Playlist</option>
                            </select>
                        </div>

                        {/* NEW: Input for Media ID */}
                        <div style={{ marginBottom: '10px' }}>
                            <input 
                                type="number" 
                                placeholder="Enter Database Media ID (e.g., 1)" 
                                value={mediaId} 
                                onChange={(e) => setMediaId(e.target.value)} 
                                style={{ width: '100%', padding: '5px' }}
                            />
                        </div>

                        <textarea 
                            placeholder="What are you listening to?" 
                            rows={4} 
                            value={content}
                            onChange={(e) => setContent(e.target.value)}
                            style={{ width: '100%', marginBottom: '10px', padding: '5px' }}
                        />
                        
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
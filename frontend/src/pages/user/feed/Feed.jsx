import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import Navbar from '../../../components/Navbar';
import styles from './Feed.module.css';

function Feed() {
    const navigate = useNavigate();
    const [showModal, setShowModal] = useState(false);

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
                    <p>Posts will appear here.</p>
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
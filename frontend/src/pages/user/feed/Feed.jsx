import styles from './Feed.module.css';
 
function Feed() {
    return (
        <div className={styles.container}>
 
            {/* Sidebar */}
            <div className={styles.sidebar}>
                <button>Profile</button>
                <button>Friends</button>
                <button>Add Friends</button>
                <button>Create Post</button>
            </div>
 
            {/* Main feed area */}
            <div className={styles.feed}>
                <h2>Feed</h2>
                <p>Posts will appear here.</p>
            </div>
 
        </div>
    );
}
 
export default Feed;
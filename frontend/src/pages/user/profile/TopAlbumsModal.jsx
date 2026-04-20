import React from 'react';
import styles from './TopAlbumsModal.module.css';

// receives isOpen (bool), onClose (function), albums (array), and listeningTime from Profile.jsx
function TopAlbumsModal({ isOpen, onClose, albums = [], listeningTime }) {

    // dont render anything if the modal is closed
    if (!isOpen) return null;

    return (
        // dark overlay behind the popup, clicking it will close the modal
        <div className={styles.overlay} onClick={onClose}>

            {/* the popup box, clicking the box wont close the modal */}
            <div className={styles.popup} onClick={e => e.stopPropagation()}>

                {/* the blank canva template as the background */}
                <img src="/3.png" alt="Top Albums Template" className={styles.template} />

                {/* data layer sits on top of the template using absolute positioning */}
                <div className={styles.dataLayer}>

                    {/* loop through top 5 albums and render each row */}
                    {albums.slice(0, 5).map((album, index) => (
                        <div key={index} className={styles.row} style={{
                            top: `${25.5 + index * 13.3}%`
                        }}>
                            {/* album art */}
                            <img
                                src={album.albumArtUrl}
                                alt={album.albumName}
                                className={styles.albumArt}
                            />

                            {/* album name */}
                            <div className={styles.songInfo}>
                                <div className={styles.songName}>
                                    {album.albumName}
                                </div>
                            </div>

                            {/* listen count */}
                            <div className={styles.listenCount}>
                                {album.listenCount ? `${album.listenCount} listens` : ''}
                            </div>
                        </div>
                    ))}

                    {/* total listening time */}
                    <div className={styles.listeningTime}>
                        {listeningTime}
                    </div>
                </div>

                {/* close button */}
                <button className={styles.closeButton} onClick={onClose}>✕</button>
            </div>
        </div>
    );
}

export default TopAlbumsModal;
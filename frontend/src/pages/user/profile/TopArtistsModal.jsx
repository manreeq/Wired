import React from 'react';
import styles from './TopArtistsModal.module.css';

// receives isOpen (bool), onClose (function), and artists (array) from Profile.jsx
function TopArtistsModal({ isOpen, onClose, artists = [], listeningTime }) {

    // dont render anything if the modal is closed
    if (!isOpen) return null;

    return (
        // dark overlay behind the popup, clicking it will close the modal
        <div className={styles.overlay} onClick={onClose}>

            {/* the popup box, clicking the box wont close the modal */}
            <div className={styles.popup} onClick={e => e.stopPropagation()}>

                {/* the blank canva template as the background */}
                <img src="/2.png" alt="Top Artists Template" className={styles.template} />

                {/* data layer sits on top of the template using absolute positioning */}
                <div className={styles.dataLayer}>

                    {/* loop through top 5 artists and render each row */}
                    {artists.slice(0, 5).map((artist, index) => (
                        <div key={index} className={styles.row} style={{
                            top: `${25.5 + index * 13.3}%`  // starting position of row 1 + index * gap between rows
                        }}>
                            {/* artist profile picture pulled from spotify */}
                            <img
                                src={artist.profilePictureUrl}
                                alt={artist.artistName}
                                className={styles.albumArt}
                            />

                            {/* artist name */}
                            <div className={styles.artistInfo}>
                                <div className={styles.artistName}>
                                    {artist.artistName}
                                </div>
                            </div>

                            {/* listen count */}
                            <div className={styles.listenCount}>
                                {artist.listenCount ? `${artist.listenCount} listens` : ''}
                            </div>
                        </div>
                    ))}
					{/* total listening time — bottom left, aligned with album photos */}
					    <div className={styles.listeningTime}>
					        {listeningTime}
					    </div>
                </div>

                {/* close button in the top right corner */}
                <button className={styles.closeButton} onClick={onClose}>✕</button>
            </div>
        </div>
    );
}

export default TopArtistsModal;
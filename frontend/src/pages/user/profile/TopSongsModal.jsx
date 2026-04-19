import React from 'react';
import styles from './TopSongsModal.module.css';

//receives isOpen (bool), onClose (function), and songs (array) from Profile.jsx
function TopSongsModal({ isOpen, onClose, songs = [] }) {

    //dont render anything if the modal is closed
    if (!isOpen) return null;

    return (
        //dark overlay behind the popup, clicking it will close the modal
        <div className={styles.overlay} onClick={onClose}>

            {/* the popup box, clicking the box wont close the modal */}
            <div className={styles.popup} onClick={e => e.stopPropagation()}>

                {/* the blank canva template as the background */}
                <img src="/1.png" alt="Top Songs Template" className={styles.template} />

                {/* data layer sits on top of the template using absolute positioning */}
                <div className={styles.dataLayer}>

                    {/* loop through top 5 songs and render each row */}
                    {songs.slice(0, 5).map((song, index) => (
                        <div key={index} className={styles.row} style={{
                            top: `${25.5 + index * 13.3}%`  // starting position of row 1 + index * gap between rows
                        }}>
                            {/* album art pulled from spotify */}
                            <img
                                src={song.albumArtUrl}
                                alt={song.songName}
                                className={styles.albumArt}
                            />

                            {/* song name and artists */}
                            <div className={styles.songInfo}>
                                <div className={styles.songName}>
                                    {song.songName}
                                </div>
                                <div className={styles.artistName}>
                                    {/* join multiple artists with a comma */}
                                    {song.artists?.join(', ')}
                                </div>
                            </div>

                            {/* listen count */}
                            <div className={styles.listenCount}>
                                {song.listenCount ? `${song.listenCount} listens` : ''}
                            </div>
                        </div>
                    ))}
                </div>

                {/* close button in the top right corner */}
                <button className={styles.closeButton} onClick={onClose}>✕</button>
            </div>
        </div>
    );
}

export default TopSongsModal;
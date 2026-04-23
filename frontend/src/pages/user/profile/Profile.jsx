import React, { useState, useEffect } from 'react';
import { useParams } from 'react-router-dom';
import Navbar from '../../../components/navbar';
import TopSongsModal from './TopSongsModal';
import TopArtistsModal from './TopArtistsModal';
import TopAlbumsModal from './TopAlbumsModal';
import styles from './Profile.module.css';

function Profile() {
    // get ID from the URL
    const { id } = useParams();

    // state to hold whoever's profile we are looking at
    const [profileData, setProfileData] = useState(null);
    const [isLoading, setIsLoading] = useState(true);

    useEffect(() => {
        // If there is an ID, we look for a friend.
        // If there is NO ID, we hit '/me' to look for ourselves.
        const endpoint = id ? `/api/users/${id}` : `/api/auth/me`;

        const fetchProfile = async () => {
            try {
                const response = await fetch(`${import.meta.env.VITE_API_BASE_URL}${endpoint}`, {
                    credentials: 'include' // This sends the secure cookie!
                });

                if (response.ok) {
                    const data = await response.json();
                    setProfileData({
                        id: data.userId,
                        displayName: data.displayName,
                        profilePicUrl: data.profilePicUrl,
                        isHistoryPrivate: data.isHistoryPrivate
                    });
                }
            } catch (error) {
                console.error("Fetch error:", error);
            } finally {
                setIsLoading(false);
            }
        };

        fetchProfile();
    }, [id]);

    const userId = id || profileData?.id;

    const apiUrl = import.meta.env.VITE_API_BASE_URL;

    //controls which modal is open
    const [showTopSongs, setShowTopSongs] = useState(false);
    const [showTopArtists, setShowTopArtists] = useState(false);
    const [showTopAlbums, setShowTopAlbums] = useState(false);

    //holds the fetched data
    const [topSongs, setTopSongs] = useState([]);
    const [topArtistsData, setTopArtistsData] = useState([]);
    const [topAlbumsData, setTopAlbumsData] = useState([]);

    const [listeningTime, setListeningTime] = useState('');
    const [range, setRange] = useState('month');

    //failsafe; holds error messages if user doesnt have enough listening history for either
    const [songsError, setSongsError] = useState('');
    const [artistsError, setArtistsError] = useState('');
    const [albumsError, setAlbumsError] = useState('');

    // Listening history state
    const [historyLimit, setHistoryLimit] = useState('5');
    const [listeningHistory, setListeningHistory] = useState([]);
    const [historyLoading, setHistoryLoading] = useState(false);
    const [historyError, setHistoryError] = useState('');

    // Fetch listening history whenever userId or limit changes
    useEffect(() => {
        if (!userId) return;
        setHistoryLoading(true);
        setHistoryError('');
        fetch(`${apiUrl}/api/feed/history/${userId}?limit=${historyLimit}`)
            .then(res => {
                if (!res.ok) throw new Error('Failed to load listening history');
                return res.json();
            })
            .then(data => setListeningHistory(data))
            .catch(err => setHistoryError(err.message))
            .finally(() => setHistoryLoading(false));
    }, [userId, historyLimit]);

    //fetch listening stats on page load
    useEffect(() => {
        const userParam = id ? `&userId=${id}` : '';
        fetch(`${apiUrl}/api/stats/listening-time?range=${range}${userParam}`, {
            credentials: 'include'
        })
        .then(res => {
            if (!res.ok) throw new Error('Failed to load listening time');
            return res.text();
        })
        .then(time => setListeningTime(time))
        .catch(err => console.error(err));
    }, [id]);

    //fetch top songs when disc button is clicked
    const handleTopSongsClick = () => {
        setSongsError('');
        const userParam = id ? `&userId=${id}` : '';
        fetch(`${apiUrl}/api/stats/top-songs?range=${range}${userParam}`, {
            credentials: 'include'
        })
        .then(res => {
            if (!res.ok) {
                return res.text().then(msg => { throw new Error(msg); });
            }
            return res.json();
        })
        .then(data => {
            setTopSongs(data);
            setShowTopSongs(true);
        })
        .catch(err => setSongsError(err.message));
    };

    // fetch top artists when disc button is clicked
    const handleTopArtistsClick = () => {
        setArtistsError('');
        const userParam = id ? `&userId=${id}` : '';
        fetch(`${apiUrl}/api/stats/top-artists?range=${range}${userParam}`, {
            credentials: 'include'
        })
        .then(res => {
            if (!res.ok) {
                return res.text().then(msg => { throw new Error(msg); });
            }
            return res.json();
        })
        .then(data => {
            setTopArtistsData(data);
            setShowTopArtists(true);
        })
        .catch(err => setArtistsError(err.message));
    };

    const handleTopAlbumsClick = () => {
        setAlbumsError('');
        const userParam = id ? `&userId=${id}` : '';
        fetch(`${apiUrl}/api/stats/top-albums?range=${range}${userParam}`, {
            credentials: 'include'
        })
        .then(res => {
            if (!res.ok) {
                return res.text().then(msg => { throw new Error(msg); });
            }
            return res.json();
        })
        .then(data => {
            setTopAlbumsData(data);
            setShowTopAlbums(true);
        })
        .catch(err => setAlbumsError(err.message));
    };

    if (isLoading) return <div className={styles.pageWrapper}><div className={styles.content}><p className={styles.loadingState}>Loading Profile...</p></div></div>;
    if (!profileData) return <div className={styles.pageWrapper}><div className={styles.content}><p className={styles.loadingState}>User not found.</p></div></div>;

    return (
        <div className={styles.pageWrapper}>
            <Navbar />
            <div className={styles.content}>

                {/* Profile Header */}
                <div className={styles.profileHeader}>
                    {profileData.profilePicUrl && profileData.profilePicUrl !== "None" ? (
                        <img
                            src={profileData.profilePicUrl}
                            alt={`${profileData.displayName}'s profile`}
                            className={styles.profileAvatar}
                        />
                    ) : (
                        <div className={styles.profileAvatarPlaceholder} />
                    )}
                    <h2 className={styles.profileName}>{profileData.displayName}</h2>
                </div>

                {/* Check if we should hide the data */}
                {(profileData.isHistoryPrivate && id) ? (
                    <div className={styles.privateBanner}>
                        <h3>This user's activity is hidden.</h3>
                    </div>
                ) : (
                    <>
                        {/* Time range selector */}
                        <div className={styles.rangeSelector}>
                            <span className={styles.rangeLabel}>Viewing stats for:</span>
                            <select
                                className={styles.rangeSelect}
                                value={range}
                                onChange={e => setRange(e.target.value)}
                            >
                                <option value="week">Last Week</option>
                                <option value="month">Last Month</option>
                                <option value="year">Last Year</option>
                                <option value="all">All Time</option>
                            </select>
                        </div>

                        {/* Disc buttons */}
                        <div className={styles.discGrid}>
                            {/* Top Songs */}
                            <div className={styles.discItem}>
                                <button onClick={handleTopSongsClick} className={styles.discBtn}>
                                    <img src="/disc.png" alt="Top Songs" className={styles.discImg} />
                                </button>
                                <span className={styles.discLabel}>Top Songs</span>
                                {songsError && <p className={styles.discError}>{songsError}</p>}
                            </div>

                            {/* Top Artists */}
                            <div className={styles.discItem}>
                                <button onClick={handleTopArtistsClick} className={styles.discBtn}>
                                    <img src="/disc.png" alt="Top Artists" className={styles.discImg} />
                                </button>
                                <span className={styles.discLabel}>Top Artists</span>
                                {artistsError && <p className={styles.discError}>{artistsError}</p>}
                            </div>

                            {/* Top Albums */}
                            <div className={styles.discItem}>
                                <button onClick={handleTopAlbumsClick} className={styles.discBtn}>
                                    <img src="/disc.png" alt="Top Albums" className={styles.discImg} />
                                </button>
                                <span className={styles.discLabel}>Top Albums</span>
                                {albumsError && <p className={styles.discError}>{albumsError}</p>}
                            </div>
                        </div>

                        {/* Top Songs Modal */}
                        <TopSongsModal
                            isOpen={showTopSongs}
                            onClose={() => setShowTopSongs(false)}
                            songs={topSongs}
                            listeningTime={listeningTime}
                            range={range}
                        />

                        {/* Top Artists Modal */}
                        <TopArtistsModal
                            isOpen={showTopArtists}
                            onClose={() => setShowTopArtists(false)}
                            artists={topArtistsData}
                            listeningTime={listeningTime}
                            range={range}
                        />

                        {/* Top Albums Modal */}
                        <TopAlbumsModal
                            isOpen={showTopAlbums}
                            onClose={() => setShowTopAlbums(false)}
                            albums={topAlbumsData}
                            listeningTime={listeningTime}
                            range={range}
                        />

                        {/* ── LISTENING HISTORY ────────────────── */}
                        <div className={styles.historySection}>
                            <div className={styles.historyHeader}>
                                <h3 className={styles.historyTitle}>Listening History</h3>
                                <select
                                    className={styles.historySelect}
                                    value={historyLimit}
                                    onChange={e => setHistoryLimit(e.target.value)}
                                >
                                    <option value="5">Last 5</option>
                                    <option value="10">Last 10</option>
                                    <option value="all">All</option>
                                </select>
                            </div>

                            {historyLoading && <p className={styles.loadingState}>Loading...</p>}
                            {historyError && <p className={styles.errorState}>{historyError}</p>}

                            {!historyLoading && !historyError && listeningHistory.length === 0 && (
                                <p className={styles.emptyState}>No listening history yet. Start playing some songs on Spotify!</p>
                            )}

                            <div className={styles.historyList}>
                                {listeningHistory.map((item, index) => (
                                    <div key={`history-${index}`} className={styles.historyItem}>
                                        <img
                                            src={item.albumArtUrl}
                                            alt={item.songTitle}
                                            className={styles.historyArt}
                                        />
                                        <div className={styles.historyInfo}>
                                            <p className={styles.historySongTitle}>{item.songTitle}</p>
                                            <p className={styles.historySongSub}>You listened to this</p>
                                        </div>
                                    </div>
                                ))}
                            </div>
                        </div>
                    </>
                )}
            </div>
        </div>
    );
}

export default Profile;
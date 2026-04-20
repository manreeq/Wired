import Navbar from '../../../components/navbar';
import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import TopSongsModal from './TopSongsModal';
import TopArtistsModal from './TopArtistsModal';
import TopAlbumsModal from './TopAlbumsModal';	

function Profile() {

    const navigate = useNavigate();

    const storedUser = JSON.parse(localStorage.getItem('wiredUser')) || {};
    const displayName = storedUser.name || "User";
    const profilePicUrl = storedUser.profilePicUrl;
    const userId = storedUser.id;

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
	    fetch(`${apiUrl}/api/stats/listening-time`, {
	        credentials: 'include'
	    })
	    .then(res => {
	        if (!res.ok) throw new Error('Failed to load listening time');
	        return res.text();
	    })
	    .then(time => setListeningTime(time))
	    .catch(err => console.error(err));
	}, []);

    //fetch top songs when disc button is clicked
    const handleTopSongsClick = () => {
        setSongsError('');
        fetch(`${apiUrl}/api/stats/top-songs`, {
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
        fetch(`${apiUrl}/api/stats/top-artists`, {
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
	    fetch(`${apiUrl}/api/stats/top-albums`, {
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

    return (
        <div>
            <Navbar />
            <div style={{ padding: '40px', fontFamily: 'sans-serif' }}>
                <nav style={{ borderBottom: '1px solid #ccc', marginBottom: '20px' }}>
                    <h1>Wired</h1>
                </nav>

                {/* profile picture and name */}
                <div style={{ display: 'flex', alignItems: 'center', gap: '20px' }}>
                    {profilePicUrl && profilePicUrl !== "None" ? (
                        <img
                            src={profilePicUrl}
                            alt={`${displayName}'s profile`}
                            style={{ width: '80px', height: '80px', borderRadius: '50%', objectFit: 'cover' }}
                        />
                    ) : (
                         //fallback if no spotify profile picture
                        <div style={{ width: '80px', height: '80px', borderRadius: '50%', backgroundColor: '#333' }}></div>
                    )}
                    <h2>{displayName}</h2>
                </div>

                {/* disc buttons for top songs and top artists */}
                <div style={{ display: 'flex', gap: '40px', marginTop: '40px' }}>

                    {/* top songs button */}
                    <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', gap: '8px' }}>
                        <button onClick={handleTopSongsClick} style={{
                            background: 'none',
                            border: 'none',
                            cursor: 'pointer',
                            padding: 0
                        }}>
                            <img src="/disc.png" alt="Top Songs" style={{ width: '80px', height: '80px' }} />
                        </button>
                        <span style={{ fontSize: '0.85rem', fontWeight: 'bold' }}>Top Songs</span>
                        {/* error shown if user hasnt listened to enough songs */}
                        {songsError && (
                            <p style={{ color: 'red', fontSize: '0.75rem', maxWidth: '120px', textAlign: 'center' }}>
                                {songsError}
                            </p>
                        )}
                    </div>

                    {/* top artists button */}
                    <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', gap: '8px' }}>
                        <button onClick={handleTopArtistsClick} style={{
                            background: 'none',
                            border: 'none',
                            cursor: 'pointer',
                            padding: 0
                        }}>
                            <img src="/disc.png" alt="Top Artists" style={{ width: '80px', height: '80px' }} />
                        </button>
                        <span style={{ fontSize: '0.85rem', fontWeight: 'bold' }}>Top Artists</span>
                        {/* error shown if user hasnt listened to enough artists */}
                        {artistsError && (
                            <p style={{ color: 'red', fontSize: '0.75rem', maxWidth: '120px', textAlign: 'center' }}>
                                {artistsError}
                            </p>
                        )}
                    </div>
					
					{/* top albums button */}
					<div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', gap: '8px' }}>
					    <button onClick={handleTopAlbumsClick} style={{
					        background: 'none',
					        border: 'none',
					        cursor: 'pointer',
					        padding: 0
					    }}>
					        <img src="/disc.png" alt="Top Albums" style={{ width: '80px', height: '80px' }} />
					    </button>
					    <span style={{ fontSize: '0.85rem', fontWeight: 'bold' }}>Top Albums</span>
					    {albumsError && (
					        <p style={{ color: 'red', fontSize: '0.75rem', maxWidth: '120px', textAlign: 'center' }}>
					            {albumsError}
					        </p>
					    )}
					</div>
                </div>

                {/* top songs modal */}
                <TopSongsModal
                    isOpen={showTopSongs}
                    onClose={() => setShowTopSongs(false)}
                    songs={topSongs}
					listeningTime={listeningTime}
                />

                {/* top artists modal */}
                <TopArtistsModal
                    isOpen={showTopArtists}
                    onClose={() => setShowTopArtists(false)}
                    artists={topArtistsData}
					listeningTime={listeningTime}
                />
				
				{/* top albums modal */}
				<TopAlbumsModal
				    isOpen={showTopAlbums}
				    onClose={() => setShowTopAlbums(false)}
				    albums={topAlbumsData}
				    listeningTime={listeningTime}
				/>

                {/* ── LISTENING HISTORY ─────────────────────────────────── */}
                <div style={{ marginTop: '50px' }}>
                    <div style={{ display: 'flex', alignItems: 'center', gap: '16px', marginBottom: '16px' }}>
                        <h3 style={{ margin: 0 }}>Listening History</h3>
                        <select
                            value={historyLimit}
                            onChange={e => setHistoryLimit(e.target.value)}
                            style={{ padding: '4px 8px', borderRadius: '6px', border: '1px solid #ccc', cursor: 'pointer' }}
                        >
                            <option value="5">Last 5</option>
                            <option value="10">Last 10</option>
                            <option value="all">All</option>
                        </select>
                    </div>

                    {historyLoading && <p>Loading...</p>}
                    {historyError && <p style={{ color: 'red' }}>{historyError}</p>}

                    {!historyLoading && !historyError && listeningHistory.length === 0 && (
                        <p style={{ color: '#888' }}>No listening history yet. Start playing some songs on Spotify!</p>
                    )}

                    <div style={{ display: 'flex', flexDirection: 'column', gap: '10px' }}>
                        {listeningHistory.map((item, index) => (
                            <div
                                key={`history-${index}`}
                                style={{
                                    display: 'flex',
                                    alignItems: 'center',
                                    gap: '12px',
                                    padding: '10px 14px',
                                    border: '1px solid #ddd',
                                    borderRadius: '8px',
                                    backgroundColor: '#fafafa'
                                }}
                            >
                                <img
                                    src={item.albumArtUrl}
                                    alt={item.songTitle}
                                    style={{ width: '48px', height: '48px', borderRadius: '4px', objectFit: 'cover' }}
                                />
                                <div>
                                    <p style={{ margin: 0, fontWeight: 'bold' }}>{item.songTitle}</p>
                                    <p style={{ margin: 0, fontSize: '0.8rem', color: '#666' }}>
                                        You listened to this
                                    </p>
                                </div>
                            </div>
                        ))}
                    </div>
                </div>

            </div>
        </div>
    );
}

export default Profile;
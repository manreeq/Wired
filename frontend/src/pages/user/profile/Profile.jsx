import React, { useState, useEffect } from 'react';
import { useParams } from 'react-router-dom';
import Navbar from '../../../components/navbar';
import TopSongsModal from './TopSongsModal';
import TopArtistsModal from './TopArtistsModal';

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
                        displayName: data.displayName,
                        profilePicUrl: data.profilePicUrl
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

    const apiUrl = import.meta.env.VITE_API_BASE_URL;

    //controls which modal is open
    const [showTopSongs, setShowTopSongs] = useState(false);
    const [showTopArtists, setShowTopArtists] = useState(false);

    //holds the fetched data
    const [topSongs, setTopSongs] = useState([]);
    const [topArtistsData, setTopArtistsData] = useState([]);

    //failsafe; holds error messages if user doesnt have enough listening history for either
    const [songsError, setSongsError] = useState('');
    const [artistsError, setArtistsError] = useState('');

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

    if (isLoading) return <div>Loading Profile...</div>;
    if (!profileData) return <div>User not found.</div>;

    return (
        <div>
            <Navbar />
            <div style={{ padding: '40px', fontFamily: 'sans-serif' }}>
                <nav style={{ borderBottom: '1px solid #ccc', marginBottom: '20px' }}>
                    <h1>Wired</h1>
                </nav>

                <div style={{ display: 'flex', alignItems: 'center', gap: '20px' }}>
                    {/* Updated to use profileData instead of the hardcoded variables */}
                    {profileData.profilePicUrl && profileData.profilePicUrl !== "None" ? (
                        <img
                            src={profileData.profilePicUrl}
                            alt={`${profileData.displayName}'s profile`}
                            style={{ width: '80px', height: '80px', borderRadius: '50%', objectFit: 'cover' }}
                        />
                    ) : (
                        <div style={{ width: '80px', height: '80px', borderRadius: '50%', backgroundColor: '#333' }}></div>
                    )}
                    <h2>{profileData.displayName}</h2>
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
                </div>

                {/* top songs modal */}
                <TopSongsModal
                    isOpen={showTopSongs}
                    onClose={() => setShowTopSongs(false)}
                    songs={topSongs}
                />

                {/* top artists modal */}
                <TopArtistsModal
                    isOpen={showTopArtists}
                    onClose={() => setShowTopArtists(false)}
                    artists={topArtistsData}
                />
            </div>
        </div>
    );
}

export default Profile;
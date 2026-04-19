import React, { useState, useEffect } from 'react';
import { useParams } from 'react-router-dom';
import Navbar from '../../../components/navbar';

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

                <div style={{ marginTop: '40px', border: '1px dashed #ccc', padding: '20px' }}>
                    <p>Profile page coming soon...</p>
                </div>
            </div>
        </div>
    );
}

export default Profile;
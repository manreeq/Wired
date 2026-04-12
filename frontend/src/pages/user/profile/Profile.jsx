import Navbar from '../../../components/Navbar';
import React from 'react';
import { useLocation } from 'react-router-dom';

function Profile() {
    //const location = useLocation();
    // grabs the name  "sent" from the Callback page
    //const displayName = location.state?.name || "User";

    const storedUser = JSON.parse(localStorage.getItem('wiredUser')) || {};
    const displayName = storedUser.name || "User";
    const profilePicUrl = storedUser.profilePicUrl;

    return (
		<div>
		<Navbar />
        <div style={{ padding: '40px', fontFamily: 'sans-serif' }}>
            <nav style={{ borderBottom: '1px solid #ccc', marginBottom: '20px' }}>
                <h1>Wired</h1>
            </nav>
            
            <div style={{ display: 'flex', alignItems: 'center', gap: '20px' }}>
                {/* Conditional Rendering for Profile Picture */}
                {profilePicUrl && profilePicUrl !== "None" ? (
                    <img 
                        src={profilePicUrl} 
                        alt={`${displayName}'s profile`} 
                        style={{ width: '80px', height: '80px', borderRadius: '50%', objectFit: 'cover' }} 
                    />
                ) : (
                    // Fallback Placeholder if no Spotify Avatar
                    <div style={{ width: '80px', height: '80px', borderRadius: '50%', backgroundColor: '#333' }}></div>
                )}
            </div>

            <div style={{ marginTop: '40px', border: '1px dashed #ccc', padding: '20px' }}>
                <p>Profile page coming soon...</p>
            </div>
        </div>
		</div>
    );
}

export default Profile;
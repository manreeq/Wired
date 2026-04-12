import Navbar from '../../../components/Navbar';
import React from 'react';
import { useLocation } from 'react-router-dom';

function Profile() {
    const location = useLocation();
    // grabs the name  "sent" from the Callback page
    const displayName = location.state?.name || "User";

    return (
		<div>
		<Navbar />
        <div style={{ padding: '40px', fontFamily: 'sans-serif' }}>
            <nav style={{ borderBottom: '1px solid #ccc', marginBottom: '20px' }}>
                <h1>Wired</h1>
            </nav>
            
            <div style={{ display: 'flex', alignItems: 'center', gap: '20px' }}>
                {/*  Profile Pic Placeholder */}
                <div style={{ width: '80px', height: '80px', borderRadius: '50%', backgroundColor: '#333' }}></div>
            </div>

            <div style={{ marginTop: '40px', border: '1px dashed #ccc', padding: '20px' }}>
                <p>Profile page coming soon...</p>
            </div>
        </div>
		</div>
    );
}

export default Profile;
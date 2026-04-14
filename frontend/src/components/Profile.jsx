import React, { useEffect, useState } from 'react';

function Profile() {
    const [user, setUser] = useState(null);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        fetch('${apiUrl}/api/auth/me', {
            credentials: 'include' // sends the cookie
        })
        .then(response => {
            if (!response.ok) throw new Error('Not logged in');
            return response.json();
        })
        .then(data => {
            setUser(data);
            setLoading(false);
        })
        .catch(() => {
            // not logged in, redirect to login
            window.location.href = '/';
        });
    }, []);

    if (loading) return <h2>Loading...</h2>;

    return (
        <div style={{ padding: '40px', fontFamily: 'sans-serif' }}>
            <nav style={{ borderBottom: '1px solid #ccc', marginBottom: '20px' }}>
                <h1>Wired</h1>
            </nav>

            <div style={{ display: 'flex', alignItems: 'center', gap: '20px' }}>
                <div style={{ width: '80px', height: '80px', borderRadius: '50%', backgroundColor: '#333' }}></div>
                <div>
                    <h2 style={{ margin: 0 }}>{user?.displayName || "User"}</h2>
                    <p style={{ color: '#666' }}>Spotify Account Connected</p>
                </div>
            </div>

            <div style={{ marginTop: '40px', border: '1px dashed #ccc', padding: '20px' }}>
                <p>Activity feed coming soon...</p>
            </div>
        </div>
    );
}

export default Profile;
import React from "react";

function Login(){
    const CLIENT_ID = import.meta.env.VITE_SPOTIFY_CLIENT_ID;
    const REDIRECT_URI = import.meta.env.VITE_SPOTIFY_REDIRECT_URI;
    const AUTH_ENDPOINT = 'https://accounts.spotify.com/authorize';
    const RESPONSE_TYPE = 'code';
    // scopes for actually getting user data later
    // SCOPES define what parts of the user's Spotify account our this code section can access.
    const SCOPES = 'user-read-private user-read-email user-read-currently-playing'; 

    // encodeURIComponent turns "http://" into "http%3A%2F%2F" so spotify can read it
    const spotifyLoginUrl = `${AUTH_ENDPOINT}?client_id=${CLIENT_ID}` + 
                            `&redirect_uri=${encodeURIComponent(REDIRECT_URI)}` + 
                            `&response_type=${RESPONSE_TYPE}` +
                            `&scope=${encodeURIComponent(SCOPES)}`;

    return (
        <div style={{ textAlign: 'center', marginTop: '20vh' }}>
            <h1>Wired</h1>
            <p>Connect your account to continue.</p>
            
            <a href={spotifyLoginUrl}>
                <button style={{ padding: '10px 20px', fontSize: '16px', cursor: 'pointer' }}>
                    Login with Spotify
                </button>
            </a>
        </div>
    );
}

export default Login;
import React from "react";

function Login(){
    const CLIENT_ID = import.meta.env.VITE_SPOTIFY_CLIENT_ID;
    const REDIRECT_URI = import.meta.env.VITE_SPOTIFY_REDIRECT_URI;
    const AUTH_ENDPOINT = 'https://accounts.spotify.com/authorize';
    const RESPONSE_TYPE = 'code';

    // Manually generates the URL that leads to the spotify login 
    const spotifyLoginUrl = `${AUTH_ENDPOINT}?client_id=${CLIENT_ID}&redirect_uri=${REDIRECT_URI}&response_type=${RESPONSE_TYPE}`;

    return (
        <div>
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
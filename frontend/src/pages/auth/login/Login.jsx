import React from "react";
import './Login.css';

function Login(){
    const CLIENT_ID = import.meta.env.VITE_SPOTIFY_CLIENT_ID;
    // const REDIRECT_URI = import.meta.env.VITE_SPOTIFY_REDIRECT_URI;
    const REDIRECT_URI = `${import.meta.env.VITE_FRONTEND_BASE_URL}/callback`;
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
        <div className="login-container">
            <h1 className="login-title">Wired</h1>
            <p className="login-subtitle">Connect your account to continue.</p>
            
            <a href={spotifyLoginUrl} className="login-link">
                <button className="spotify-btn">
                    Login with Spotify
                </button>
            </a>
        </div>
    );
}

export default Login;
// This code file essentially just handles returning to the Wired app after logging in via 
// the spotify login portal, and collects the code found in the url need


import React, { useEffect, useState } from 'react';
import { useSearchParams, useNavigate } from 'react-router-dom';

function Callback() {
    // searchParams lets the code look into urls (like looking for ?code=...)
    const [searchParams] = useSearchParams();
    
    // navigate is a function we call later to send the user to the Dashboard
    const navigate = useNavigate();
    
    // status is a variable tied to the screen. setStatus is the function to change it
    const [status, setStatus] = useState('Processing login...');

    // the code inside useEffect runs automatically the  moment page loads on the screen
    useEffect(() => {        
        // looks at the URL in the browser address bar and grabs whatever is after "code="
        // this code is the access token
        const code = searchParams.get('code');

        // if found a code in the URL, run the login process
        if (code) {
            // send a network request to AuthController
 			fetch(import.meta.env.VITE_SPOTIFY_REDIRECT_URI_CALLBACK, {
			    method: 'POST',
			    headers: { 'Content-Type': 'application/json' },
			    body: JSON.stringify({ code: code }),
			    credentials: 'include' 
			})
            // .then() runs after it finishes processing the request
            .then(response => {
                // if it returns an error, throw error 
                if (!response.ok) {
                    throw new Error('Backend rejected the login');
                }
                // otherwise, extract the text response 
                return response.json(); 
            })
            // 'data' holds the final JSON object from the backend
            .then(data => {
                setStatus(data.message); // "Successfully logged in as [Name]" string

                // Save the user data globally to the browser
                const userData = {
                    id: data.userId,
                    name: data.displayName,
                    friendCode: data.friendCode,
                    profilePicUrl: data.profilePicUrl
                };
                localStorage.setItem('wiredUser', JSON.stringify(userData));

                // wait 1.5 seconds so the user sees the success message, then move to profile
				setTimeout(() => {
				    navigate('/feed');
				}, 1500);})
            // if something goes wrong (network crash, backend error), catch it here
			.catch(error => {
			    setStatus('Login failed. Please try again.');
			    console.error('Error during login:', error);
			    
			    // wait 2 seconds so they can see the error, then send back to login
			    setTimeout(() => {
			        navigate('/');
			    }, 2000);
			});
            
        } else {
            // if the user somehow loaded /callback without a code in the URL
            setStatus('No authorization code found in the URL.');
        }
        
    // array tells React when to re-run this; only want it to run when these tools load
    }, [searchParams, navigate]);

    return (
        <div style={{ display: 'flex', justifyContent: 'center', marginTop: '20vh' }}>
            {/* We output whatever text is currently stored in our 'status' variable */}
            <h2>{status}</h2>
        </div>
    );
}

export default Callback;
import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import styles from './navbar.module.css';

function Navbar() {
    
    const navigate = useNavigate();

    const [isMenuOpen, setIsMenuOpen] = useState(false);
    const [isPrivate, setIsPrivate] = useState(false);
    const [showDeleteModal, setShowDeleteModal] = useState(false);

    const handleLogout = () => {
        localStorage.clear();   // -------- NEED TO CHANGE INTO COOKIES -------- 
        navigate('/'); 
    };

    const handlePrivacyToggle = async () => {
        setIsPrivate(!isPrivate); 
        // TODO: Add PUT /api/posts/privacy fetch call here
    };

    const handleDeleteAccount = async () => {
        try {
            const response = await fetch(`${import.meta.env.VITE_API_BASE_URL}/api/auth/me`, {
                method: 'DELETE',
                // includes the auth cookies
                credentials: 'include' 
            });

            if (response.ok) {
                console.log("Account deleted");
                setShowDeleteModal(false);
                navigate('/'); 
            } else {
                console.error("Backend refused to delete the account");
                alert("Something went wrong trying to delete your account");
            }
        } catch (error) {
            console.error("Network error. Is Spring Boot running?", error);
        }
    };


    return (
        <nav className={styles.navbar}>
            
            {/* LEFT SIDE: Your existing navigation links */}
            <div className={styles.navLinks}>
                {/* onClick triggers a function. When clicked, it tells the router to go to '/feed' */}
                <button className={styles.navBtn} onClick={() => navigate('/feed')}>Home</button>
                <button className={styles.navBtn} onClick={() => navigate('/profile')}>Profile</button>
            </div>

            {/* RIGHT SIDE: The Settings Dropdown */}
            <div className={styles.settingsContainer}>
                
                {/* The Gear Icon Button */}
                <button 
                    className={styles.settingsIconBtn} 
                    // When clicked, flip the menu switch. If it's open, close it. If it's closed, open it.
                    onClick={() => setIsMenuOpen(!isMenuOpen)}
                >
                    ⚙️
                </button>

                {/* The Floating Menu */}
                {/* The '&&' is a React trick. It means: "If isMenuOpen is TRUE, then draw the HTML below. If FALSE, draw nothing." */}
                {isMenuOpen && (
                    <div className={styles.dropdownMenu}>
                        
                        {/* Privacy Button */}
                        <button onClick={handlePrivacyToggle} className={styles.menuItem}>
                            {/* The '?' is a shortcut for if/else. If isPrivate is true, show 'Show Profile'. Else, show 'Hide Profile' */}
                            <span>{isPrivate ? 'Show Profile' : 'Hide Profile'}</span>
                        </button>
                        
                        {/* Delete Button */}
                        <button onClick={() => {
                            // 1. Turn ON the big red warning popup
                            setShowDeleteModal(true);
                            // 2. Turn OFF (close) the small dropdown menu so it gets out of the way
                            setIsMenuOpen(false); 
                        }} className={styles.menuItem}>
                            <span>Delete Account</span>
                        </button>
                        
                        {/* Just a horizontal line to make the menu look nice */}
                        <hr className={styles.menuDivider} />
                        
                        {/* Logout Button */}
                        <button onClick={handleLogout} className={styles.menuItem}>
                            <span>Logout</span>
                        </button>
                    </div>
                )}
            </div>

            {/* THE DELETE MODAL (Floats over the whole screen) */}
            {/* Again, "If showDeleteModal is TRUE, draw this warning box. Otherwise, it doesn't exist." */}
            {showDeleteModal && (
                <div className={styles.modalOverlay}>
                    <div className={styles.modal}>
                        <h3>Are you sure?</h3>
                        <p>This will permanently erase your data and disconnect Spotify.</p>
                        
                        <div className={styles.modalActions}>
                            {/* If they click Cancel, flip the switch back to FALSE to hide this popup. */}
                            <button onClick={() => setShowDeleteModal(false)}>Cancel</button>
                            
                            {/* If they click Confirm, run the 'handleDeleteAccount' function we made earlier. */}
                            <button onClick={handleDeleteAccount} className={styles.confirmBtn}>
                                Confirm
                            </button>
                        </div>
                    </div>
                </div>
            )}
        </nav>
    );
}

export default Navbar;
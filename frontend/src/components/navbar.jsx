import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import styles from './navbar.module.css';

function Navbar() {
    
    const navigate = useNavigate();
    const storedUser = JSON.parse(localStorage.getItem('wiredUser')) || {};

    const [isMenuOpen, setIsMenuOpen] = useState(false);
    const [isPrivate, setIsPrivate] = useState(storedUser.isHistoryPrivate || false);
    const [showDeleteModal, setShowDeleteModal] = useState(false);

    const handleLogout = () => {
        localStorage.clear();   // -------- NEED TO CHANGE INTO COOKIES -------- 
        navigate('/'); 
    };

    const handlePrivacyToggle = async () => {
        const targetId = storedUser.id || storedUser.userID;

        if (!targetId) {
            console.error("No user ID found in localStorage!");
            return;
        }

        const newPrivacyState = !isPrivate;
        setIsPrivate(newPrivacyState); 

        try {
            const response = await fetch(`${import.meta.env.VITE_API_BASE_URL}/api/users/${targetId}/privacy`, {
                method: 'PUT',
                headers: { 'Content-Type': 'application/json' },
                credentials: 'include',
                body: JSON.stringify({ isHistoryPrivate: newPrivacyState })
            });

            if (response.ok) {
                const updatedUser = { ...storedUser, isHistoryPrivate: newPrivacyState };
                localStorage.setItem('wiredUser', JSON.stringify(updatedUser));
                console.log("Privacy setting saved to database!");
            } else {
                setIsPrivate(!newPrivacyState);
                console.error("Backend refused to update privacy");
            }
        } catch (error) {
            setIsPrivate(!newPrivacyState);
            console.error("Network error saving privacy setting:", error);
        }
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
            
            <div className={styles.navLinks}>
                {/* onClick triggers a function. When clicked, it tells the router to go to '/feed' */}
                <button className={styles.navBtn} onClick={() => navigate('/feed')}>Home</button>
                <button className={styles.navBtn} onClick={() => navigate('/profile')}>Profile</button>
            </div>

            <div className={styles.settingsContainer}>
                
                <button 
                    className={styles.settingsIconBtn} 
                    onClick={() => setIsMenuOpen(!isMenuOpen)}
                >
                    ⚙️
                </button>

                {isMenuOpen && (
                    <div className={styles.dropdownMenu}>
                        
                        {/* Privacy Button */}
                        <button onClick={handlePrivacyToggle} className={styles.menuItem}>
                            <span>{isPrivate ? 'Show Profile' : 'Hide Profile'}</span>
                        </button>
                        
                        {/* Delete Button */}
                        <button onClick={() => {
                            setShowDeleteModal(true);
                            setIsMenuOpen(false); 
                        }} className={styles.menuItem}>
                            <span>Delete Account</span>
                        </button>
                        
                        <hr className={styles.menuDivider} />
                        
                        {/* Logout Button */}
                        <button onClick={handleLogout} className={styles.menuItem}>
                            <span>Logout</span>
                        </button>
                    </div>
                )}
            </div>

            {showDeleteModal && (
                <div className={styles.modalOverlay}>
                    <div className={styles.modal}>
                        <h3>Are you sure?</h3>
                        <p>This will permanently erase your data and disconnect Spotify.</p>
                        
                        <div className={styles.modalActions}>
                            <button onClick={() => setShowDeleteModal(false)}>Cancel</button>
                            
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
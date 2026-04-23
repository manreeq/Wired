import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import styles from './navbar.module.css';

const getCookie = (name) => {
    const value = `; ${document.cookie}`;
    const parts = value.split(`; ${name}=`);
    if (parts.length === 2) {
        try {
            return JSON.parse(decodeURIComponent(parts.pop().split(';').shift()));
        } catch (e) {
            return {};
        }
    }
    return {};
};

const setCookie = (name, value, days = 7) => {
    const expires = new Date(Date.now() + days * 864e5).toUTCString();
    document.cookie = `${name}=${encodeURIComponent(JSON.stringify(value))}; expires=${expires}; path=/`;
};

const deleteCookie = (name) => {
    document.cookie = `${name}=; expires=Thu, 01 Jan 1970 00:00:00 UTC; path=/;`;
};


function Navbar() {
    const navigate = useNavigate();
    const storedUser = getCookie('wiredUser');

    const [isMenuOpen, setIsMenuOpen] = useState(false);
    const [isPrivate, setIsPrivate] = useState(storedUser.isHistoryPrivate || false);
    const [showDeleteModal, setShowDeleteModal] = useState(false);

    const handleLogout = async () => {
        try {
            await fetch(`${import.meta.env.VITE_API_BASE_URL}/api/auth/logout`, {
                method: 'POST',
                credentials: 'include' 
            });
        } catch (e) {
            console.error("Backend logout failed", e);
        }

        deleteCookie('wiredUser'); 
        navigate('/'); 
    };

    const handlePrivacyToggle = async () => {
        const targetId = storedUser.userId || storedUser.id || storedUser.userID;

        if (!targetId) {
            console.error("No user ID found in cookies!");
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
                // update the cookie instead of localStorage
                const updatedUser = { ...storedUser, isHistoryPrivate: newPrivacyState };
                setCookie('wiredUser', updatedUser);
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
                credentials: 'include' 
            });

            if (response.ok) {
                console.log("Account deleted");
                setShowDeleteModal(false);
                deleteCookie('wiredUser');
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

            {/* All nav items centered in one group */}
            <div className={styles.navLinks}>
                <button className={styles.navBtn} onClick={() => navigate('/feed')}>Home</button>
                <button className={styles.navBtn} onClick={() => navigate('/profile')}>Profile</button>

                {/* Settings — same style as other nav buttons, with dropdown */}
                <div className={styles.settingsContainer}>
                    <button
                        className={styles.navBtn}
                        onClick={() => setIsMenuOpen(!isMenuOpen)}
                    >
                        Settings
                    </button>

                    {isMenuOpen && (
                        <div className={styles.dropdownMenu}>

                            <button onClick={handlePrivacyToggle} className={styles.menuItem}>
                                <span>{isPrivate ? 'Show Profile' : 'Hide Profile'}</span>
                            </button>

                            <button onClick={() => {
                                setShowDeleteModal(true);
                                setIsMenuOpen(false);
                            }} className={styles.menuItem}>
                                <span>Delete Account</span>
                            </button>

                            <hr className={styles.menuDivider} />

                            <button onClick={handleLogout} className={styles.menuItem}>
                                <span>Logout</span>
                            </button>
                        </div>
                    )}
                </div>
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
import { useNavigate } from 'react-router-dom';
import styles from './navbar.module.css';
 
function Navbar() {
    const navigate = useNavigate();
 
    return (
        <nav className={styles.navbar}>
            <button onClick={() => navigate('/feed')}>Home</button>
            <button onClick={() => navigate('/profile')}>Profile</button>
			<button onClick={() => navigate('/posthistory')}>Post History</button>
        </nav>
    );
}
 
export default Navbar;
 
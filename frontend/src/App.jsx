import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import Login from './pages/auth/login/Login';
import Callback from './pages/auth/callback/Callback';
import Profile from './pages/user/profile/Profile';
import Feed from './pages/user/feed/Feed'

function App() {
  return (
    <Router>
      <Routes>
        {/*  handles http://127.0.0.1:5173/ */}
        <Route path="/" element={<Login />} />

        {/* handles http://127.0.0.1:5173/callback */}
        <Route path="/callback" element={<Callback />} />

        {/* for viewing your own profile */}
        <Route path="/profile" element={<Profile />} />

        {/* for viewing someone else */}
        <Route path="/profile/:id" element={<Profile />} />
		<Route path="/feed" element={<Feed />} />
		 
      </Routes>
    </Router>
  );
}

export default App;
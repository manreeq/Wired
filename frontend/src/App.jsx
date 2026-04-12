import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import Login from './pages/auth/login/Login';
import Callback from './pages/auth/callback/Callback';
import Profile from './pages/user/profile/Profile';
import Feed from './pages/user/feed/Feed'
import PostHistory from './pages/user/history/PostHistory'

function App() {
  return (
    <Router>
      <Routes>
        {/*  handles http://127.0.0.1:5173/ */}
        <Route path="/" element={<Login />} />

        {/* handles http://127.0.0.1:5173/callback */}
        <Route path="/callback" element={<Callback />} />

        <Route path="/profile" element={<Profile />} />
		<Route path="/feed" element={<Feed />} />
		<Route path="/posthistory" element={<PostHistory />} />
		 
      </Routes>
    </Router>
  );
}

export default App;
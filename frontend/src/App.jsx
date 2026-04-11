import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import Login from './pages/auth/Login/Login';
import Callback from './pages/auth/Callback/Callback';
import Profile from './pages/user/Profile/Profile';
import Feed from './pages/user/Feed/Feed'

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
      </Routes>
    </Router>
  );
}

export default App;
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import toast, { Toaster, ToastBar } from 'react-hot-toast';
import Login from './pages/auth/login/Login';
import Callback from './pages/auth/callback/Callback';
import Profile from './pages/user/profile/Profile';
import Feed from './pages/user/feed/Feed'

function App() {
  return (
    <Router>
      <Toaster
        position="bottom-center"
        reverseOrder={false}
        toastOptions={{
          style: {
            background: '#1a1a1a',
            color: '#ffffff',
            border: '1px solid #333333',
            borderRadius: '12px',
            fontFamily: "'Segoe UI', Roboto, Helvetica, Arial, sans-serif",
          },
          success: {
            iconTheme: { primary: '#1DB954', secondary: '#121212' },
          },
        }}
      >
        {(t) => (
          <ToastBar toast={t}>
            {({ icon, message }) => (
              <>
                {icon}
                {message}
                <button
                  onClick={() => toast.dismiss(t.id)}
                  style={{
                    background: 'none',
                    border: 'none',
                    color: '#888',
                    cursor: 'pointer',
                    fontSize: '16px',
                    lineHeight: 1,
                    padding: '0 4px',
                    flexShrink: 0,
                  }}
                  title="Dismiss"
                >✕</button>
              </>
            )}
          </ToastBar>
        )}
      </Toaster>
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
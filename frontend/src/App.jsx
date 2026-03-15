import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import Login from './components/Login';
import Callback from './components/Callback';

function App() {
  return (
    <Router>
      <Routes>
        {/*  handles http://127.0.0.1:5173/ */}
        <Route path="/" element={<Login />} />

        {/* handles http://127.0.0.1:5173/callback */}
        <Route path="/callback" element={<Callback />} />
      </Routes>
    </Router>
  );
}

export default App;
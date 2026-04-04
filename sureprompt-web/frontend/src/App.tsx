import { Routes, Route, Link, useLocation } from 'react-router-dom'
import { useState, useEffect } from 'react'
import axios from 'axios'
import { Home, Compass, User, LogOut } from 'lucide-react'
import HomePage from './pages/HomePage'
import ExplorePage from './pages/ExplorePage'
import ProfilePage from './pages/ProfilePage'
import PromptDetailPage from './pages/PromptDetailPage'

import LoginPage from './pages/LoginPage'

function App() {
  const [currentUser, setCurrentUser] = useState<any>(null);
  const [loading, setLoading] = useState(true);
  const location = useLocation();

  useEffect(() => {
    // Check initial auth status
    axios.get('/api/auth/me')
      .then(res => setCurrentUser(res.data))
      .catch(() => setCurrentUser(null))
      .finally(() => setLoading(false));
  }, []);

  if (loading) {
    return (
      <div style={{ display: 'flex', height: '100vh', alignItems: 'center', justifyContent: 'center' }}>
        <div className="loading-spinner gradient-text" style={{ fontSize: '1.5rem', fontWeight: 700 }}>
          SURE PROMPT
        </div>
      </div>
    );
  }

  return (
    <div className="app-container">
      {/* Navigation */}
      <nav className="glass" style={{ position: 'sticky', top: 0, zIndex: 100, padding: '0.8rem 2rem', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        <Link to="/" className="gradient-text" style={{ fontSize: '1.5rem', fontWeight: 800, textDecoration: 'none' }}>
          SURE PROMPT
        </Link>
        <div style={{ display: 'flex', gap: '1.5rem', alignItems: 'center' }}>
          <Link to="/" className={`nav-link ${location.pathname === '/' ? 'active' : ''}`}><Home size={20} /> Home</Link>
          <Link to="/explore" className={`nav-link ${location.pathname === '/explore' ? 'active' : ''}`}><Compass size={20} /> Explore</Link>
          {currentUser ? (
            <>
              <Link to={`/profile/${currentUser.username}`} className="nav-link"><User size={20} /> {currentUser.username}</Link>
              <form action="/logout" method="post" style={{ display: 'inline' }}>
                <button type="submit" className="glass-btn btn" style={{ backgroundColor: 'transparent', border: 'none', color: 'var(--text-muted)' }}>
                  <LogOut size={20} />
                </button>
              </form>
            </>
          ) : (
            <Link to="/login" className="btn sm">Login</Link>
          )}
        </div>
      </nav>

      <main className="container" style={{ paddingTop: '2rem' }}>
        <Routes>
          <Route path="/" element={<HomePage />} />
          <Route path="/explore" element={<ExplorePage />} />
          <Route path="/prompts/:id" element={<PromptDetailPage />} />
          <Route path="/profile/:username" element={<ProfilePage />} />
          <Route path="/login" element={<LoginPage />} />
        </Routes>
      </main>

      <style>{`
        .nav-link {
          display: flex;
          align-items: center;
          gap: 8px;
          color: var(--text-secondary);
          padding: 8px 12px;
          border-radius: var(--radius-sm);
          transition: all 0.2s;
        }
        .nav-link:hover, .nav-link.active {
          color: var(--text-primary);
          background: rgba(255, 255, 255, 0.05);
        }
        .btn.sm {
          padding: 0.4rem 1rem;
          font-size: 0.9rem;
        }
      `}</style>
    </div>
  )
}

export default App

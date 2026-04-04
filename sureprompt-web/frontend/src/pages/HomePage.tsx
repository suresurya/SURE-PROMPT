import { useEffect, useState } from 'react'
import axios from 'axios'
import PromptCard from '../components/PromptCard'
import { PlusCircle, TrendingUp, Users, LayoutGrid } from 'lucide-react'

const HomePage = ({ currentUserId }: { currentUserId?: number }) => {
  const [prompts, setPrompts] = useState<any[]>([]);
  const [loading, setLoading] = useState(true);
  const [activeTab, setActiveTab] = useState('all');

  useEffect(() => {
    setLoading(true);
    axios.get(`/api/feed?tab=${activeTab}`)
      .then(res => setPrompts(res.data.prompts))
      .catch(err => console.error(err))
      .finally(() => setLoading(false));
  }, [activeTab]);

  return (
    <div style={{ display: 'grid', gridTemplateColumns: '250px 1fr 300px', gap: '2rem' }}>
      {/* Sidebar Left */}
      <aside style={{ position: 'sticky', top: '100px', height: 'fit-content' }}>
        <div style={{ display: 'flex', flexDirection: 'column', gap: '0.5rem' }}>
          <button 
            onClick={() => setActiveTab('all')}
            className={`sidebar-btn ${activeTab === 'all' ? 'active' : ''}`}
          >
            <LayoutGrid size={20} /> All Prompts
          </button>
          <button 
            onClick={() => setActiveTab('following')}
            className={`sidebar-btn ${activeTab === 'following' ? 'active' : ''}`}
          >
            <Users size={20} /> Following
          </button>
          <button 
            onClick={() => setActiveTab('trending')}
            className={`sidebar-btn ${activeTab === 'trending' ? 'active' : ''}`}
          >
            <TrendingUp size={20} /> Trending
          </button>
        </div>
      </aside>

      {/* Main Feed */}
      <div style={{ display: 'flex', flexDirection: 'column', gap: '1.5rem' }}>
        <div className="glass" style={{ padding: '1.5rem', borderRadius: 'var(--radius-md)', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
          <h2 style={{ fontSize: '1.2rem' }}>Latest Prompts</h2>
          <button style={{ background: 'var(--primary)', color: 'white', display: 'flex', alignItems: 'center', gap: '8px', padding: '0.6rem 1.2rem' }}>
            <PlusCircle size={20} /> Share Prompt
          </button>
        </div>

        {loading ? (
          <div style={{ textAlign: 'center', padding: '3rem' }} className="text-secondary">
            Loading amazing prompts...
          </div>
        ) : (
          <div style={{ display: 'flex', flexDirection: 'column', gap: '1.5rem' }}>
            {prompts.map(p => (
              <PromptCard key={p.id} prompt={p} currentUserId={currentUserId} />
            ))}
            {prompts.length === 0 && (
              <div style={{ textAlign: 'center', padding: '4rem', color: 'var(--text-muted)' }}>
                No prompts found in this tab.
              </div>
            )}
          </div>
        )}
      </div>

      {/* Sidebar Right */}
      <aside style={{ position: 'sticky', top: '100px', height: 'fit-content' }}>
        <div className="premium-card" style={{ padding: '1.5rem' }}>
          <h3 style={{ fontSize: '1.1rem', marginBottom: '1rem' }}>Community Challenges</h3>
          <p style={{ color: 'var(--text-secondary)', fontSize: '0.9rem', marginBottom: '1rem' }}>
            Current Focus: **System Design (Level 5)**
          </p>
          <button style={{ width: '100%', background: 'var(--bg-elevated)', color: 'var(--text-primary)' }}>
            Join Discussion
          </button>
        </div>
      </aside>

      <style>{`
        .sidebar-btn {
          display: flex;
          align-items: center;
          gap: 12px;
          background: transparent;
          color: var(--text-secondary);
          padding: 12px 16px;
          border-radius: var(--radius-md);
          text-align: left;
          width: 100%;
          border: 1px solid transparent;
        }
        .sidebar-btn:hover {
          background: rgba(255, 255, 255, 0.05);
          color: var(--text-primary);
        }
        .sidebar-btn.active {
          background: rgba(124, 58, 237, 0.1);
          color: var(--primary);
          border-color: rgba(124, 58, 237, 0.2);
        }
      `}</style>
    </div>
  )
}

export default HomePage

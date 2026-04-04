import { useEffect, useState } from 'react'
import axios from 'axios'
import PromptCard from '../components/PromptCard'
import { Search, Filter, SlidersHorizontal, CheckCircle2 } from 'lucide-react'

const ExplorePage = ({ currentUserId }: { currentUserId?: number }) => {
  const [prompts, setPrompts] = useState<any[]>([]);
  const [loading, setLoading] = useState(false);
  const [query, setQuery] = useState('');
  const [difficulty, setDifficulty] = useState('');
  const [platform, setPlatform] = useState('');
  const [verifiedOnly, setVerifiedOnly] = useState(false);

  const handleSearch = async () => {
    setLoading(true);
    try {
      const res = await axios.get('/api/search', {
        params: {
          q: query,
          difficulty,
          platform,
          verifiedOnly
        }
      });
      setPrompts(res.data.results || []);
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    handleSearch();
  }, [difficulty, platform, verifiedOnly]);

  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: '2rem' }}>
      {/* Search Header */}
      <div className="glass" style={{ padding: '3rem', borderRadius: 'var(--radius-lg)', textAlign: 'center', background: 'linear-gradient(135deg, rgba(20,20,23,0.8), rgba(124,58,237,0.05))' }}>
        <h1 className="gradient-text" style={{ fontSize: '2.5rem', marginBottom: '1rem', fontWeight: 800 }}>Explore Prompts</h1>
        <p style={{ color: 'var(--text-secondary)', marginBottom: '2rem' }}>Discover high-quality prompts for your favorite AI models.</p>
        
        <div style={{ maxWidth: '600px', margin: '0 auto', position: 'relative' }}>
          <Search style={{ position: 'absolute', left: '20px', top: '50%', transform: 'translateY(-50%)', color: 'var(--text-muted)' }} size={20} />
          <input 
            type="text" 
            placeholder="Search by topic, framework, or model..."
            className="search-input"
            value={query}
            onChange={(e) => setQuery(e.target.value)}
            onKeyDown={(e) => e.key === 'Enter' && handleSearch()}
          />
        </div>
      </div>

      {/* Filters & Content */}
      <div style={{ display: 'grid', gridTemplateColumns: '300px 1fr', gap: '2rem' }}>
        <aside className="premium-card" style={{ padding: '1.5rem', height: 'fit-content', position: 'sticky', top: '100px' }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: '10px', marginBottom: '1.5rem', borderBottom: '1px solid var(--border)', paddingBottom: '1rem' }}>
            <Filter size={18} /> <h3 style={{ fontSize: '1.1rem' }}>Filters</h3>
          </div>

          <div style={{ display: 'flex', flexDirection: 'column', gap: '1.5rem' }}>
            <div className="filter-group">
              <label>Difficulty</label>
              <select value={difficulty} onChange={(e) => setDifficulty(e.target.value)}>
                <option value="">All Levels</option>
                <option value="EASY">Easy</option>
                <option value="MEDIUM">Medium</option>
                <option value="HARD">Hard</option>
              </select>
            </div>

            <div className="filter-group">
              <label>AI Platform</label>
              <select value={platform} onChange={(e) => setPlatform(e.target.value)}>
                <option value="">All Platforms</option>
                <option value="ChatGPT (GPT-4)">ChatGPT</option>
                <option value="Claude 3 Opus">Claude</option>
                <option value="Gemini Advanced">Gemini</option>
              </select>
            </div>

            <div className="filter-group" style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginTop: '1rem' }}>
              <span style={{ fontSize: '0.9rem', color: 'var(--text-secondary)' }}>AI Verified Only</span>
              <label className="switch">
                <input type="checkbox" checked={verifiedOnly} onChange={(e) => setVerifiedOnly(e.target.checked)} />
                <span className="slider"></span>
              </label>
            </div>
          </div>
        </aside>

        <div style={{ minHeight: '400px' }}>
          {loading ? (
            <div style={{ textAlign: 'center', paddingTop: '4rem' }}>Searching...</div>
          ) : (
            <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(300px, 1fr))', gap: '1.5rem' }}>
              {prompts.map(p => (
                <PromptCard key={p.id} prompt={p} currentUserId={currentUserId} />
              ))}
              {prompts.length === 0 && !loading && (
                <div style={{ gridColumn: '1/-1', textAlign: 'center', padding: '5rem', color: 'var(--text-muted)' }}>
                  No results found for your search.
                </div>
              )}
            </div>
          )}
        </div>
      </div>

      <style>{`
        .search-input {
          width: 100%;
          padding: 1.2rem 1.2rem 1.2rem 3.5rem;
          background: var(--bg-elevated);
          border: 1px solid var(--border);
          border-radius: 50px;
          color: white;
          font-size: 1.1rem;
          transition: all 0.2s;
        }
        .search-input:focus {
          outline: none;
          border-color: var(--primary);
          box-shadow: 0 0 0 4px rgba(124, 58, 237, 0.1);
        }
        .filter-group label {
          display: block;
          margin-bottom: 0.5rem;
          font-size: 0.85rem;
          color: var(--text-muted);
          text-transform: uppercase;
          letter-spacing: 0.5px;
        }
        .filter-group select {
          width: 100%;
          background: var(--bg-elevated);
          border: 1px solid var(--border);
          padding: 10px;
          border-radius: var(--radius-sm);
          color: white;
        }
        
        /* Switch styling */
        .switch { position: relative; display: inline-block; width: 44px; height: 24px; }
        .switch input { opacity: 0; width: 0; height: 0; }
        .slider { position: absolute; cursor: pointer; top: 0; left: 0; right: 0; bottom: 0; background-color: #27272a; transition: .4s; border-radius: 34px; }
        .slider:before { position: absolute; content: ""; height: 18px; width: 18px; left: 3px; bottom: 3px; background-color: white; transition: .4s; border-radius: 50%; }
        input:checked + .slider { background-color: var(--primary); }
        input:checked + .slider:before { transform: translateX(20px); }
      `}</style>
    </div>
  );
};

export default ExplorePage;

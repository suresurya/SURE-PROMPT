import { useEffect, useState } from 'react'
import { useParams, Link } from 'react-router-dom'
import axios from 'axios'
import PromptCard from '../components/PromptCard'
import { User, Award, Flame, MessageSquare, Plus, Check, Settings } from 'lucide-react'

const ProfilePage = ({ currentUserId }: { currentUserId?: number }) => {
  const { username } = useParams();
  const [profile, setProfile] = useState<any>(null);
  const [prompts, setPrompts] = useState<any[]>([]);
  const [loading, setLoading] = useState(true);
  const [isFollowing, setIsFollowing] = useState(false);

  useEffect(() => {
    setLoading(true);
    // Profile info
    axios.get(`/api/users/${username}`)
      .then(res => {
        setProfile(res.data);
        setIsFollowing(res.data.isFollowing);
      })
      .catch(err => console.error(err));

    // User prompts
    axios.get(`/api/feed?tab=user&username=${username}`)
      .then(res => setPrompts(res.data.prompts))
      .catch(err => console.error(err))
      .finally(() => setLoading(false));
  }, [username]);

  const handleFollow = () => {
    setIsFollowing(!isFollowing);
    // Real API call here
  };

  if (loading && !profile) {
    return <div style={{ textAlign: 'center', padding: '5rem' }}>Loading Profile...</div>;
  }

  if (!profile) {
    return <div style={{ textAlign: 'center', padding: '5rem' }}>User not found.</div>;
  }

  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: '3rem' }}>
      {/* Profile Header */}
      <div className="premium-card" style={{ padding: '3rem', position: 'relative' }}>
        <div style={{ position: 'absolute', top: 0, left: 0, right: 0, height: '120px', background: 'linear-gradient(90deg, #7c3aed, #f43f5e)', borderRadius: 'var(--radius-md) var(--radius-md) 0 0', opacity: 0.2 }}></div>
        
        <div style={{ display: 'flex', gap: '2.5rem', alignItems: 'end', position: 'relative', zIndex: 1 }}>
          <div style={{ width: '150px', height: '150px', borderRadius: '50%', background: 'var(--bg-elevated)', border: '4px solid var(--bg-surface)', display: 'flex', alignItems: 'center', justifyContent: 'center', fontSize: '3rem', fontWeight: 800 }}>
            {profile.avatarUrl ? <img src={profile.avatarUrl} style={{ width: '100%', height: '100%', borderRadius: '50%' }} /> : profile.username.charAt(0).toUpperCase()}
          </div>
          
          <div style={{ flex: 1, paddingBottom: '1rem' }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
              <div>
                <h1 style={{ fontSize: '2rem', marginBottom: '0.2rem' }}>{profile.displayName || profile.username}</h1>
                <p style={{ color: 'var(--text-secondary)' }}>@{profile.username}</p>
              </div>
              <div style={{ display: 'flex', gap: '10px' }}>
                {profile.isOwnProfile ? (
                  <button className="btn secondary" style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
                    <Settings size={18} /> Edit Profile
                  </button>
                ) : (
                  <button 
                    onClick={handleFollow}
                    className={`btn ${isFollowing ? 'secondary' : 'primary'}`}
                    style={{ 
                      minWidth: '120px', 
                      display: 'flex', 
                      alignItems: 'center', 
                      justifyContent: 'center', 
                      gap: '8px', 
                      background: isFollowing ? 'rgba(255,255,255,0.05)' : 'var(--primary)',
                      color: isFollowing ? 'var(--text-secondary)' : 'white'
                    }}
                  >
                    {isFollowing ? <><Check size={18} /> Following</> : <><Plus size={18} /> Follow</>}
                  </button>
                )}
              </div>
            </div>
            
            <p style={{ marginTop: '1rem', color: 'var(--text-primary)', maxWidth: '600px' }}>
              {profile.bio || 'Sharing high-quality technical prompts for the community.'}
            </p>
            
            <div style={{ display: 'flex', gap: '2rem', marginTop: '1.5rem' }}>
              <div className="stat-item">
                <span className="stat-value">{profile.totalPrompts || 0}</span>
                <span className="stat-label">Prompts</span>
              </div>
              <div className="stat-item">
                <span className="stat-value">{profile.totalLikes || 0}</span>
                <span className="stat-label">Likes Received</span>
              </div>
              <div className="stat-item">
                <div style={{ display: 'flex', alignItems: 'center', gap: '6px' }}>
                  <Flame size={18} color="#f97316" fill="#f97316" />
                  <span className="stat-value">{profile.streakCount || 0}</span>
                </div>
                <span className="stat-label">Day Streak</span>
              </div>
            </div>
          </div>
        </div>
      </div>

      {/* User Content */}
      <div style={{ display: 'grid', gridTemplateColumns: 'minmax(0, 1fr) 350px', gap: '2rem' }}>
        <div style={{ display: 'flex', flexDirection: 'column', gap: '1.5rem' }}>
          <div style={{ display: 'flex', borderBottom: '1px solid var(--border)', gap: '2rem', marginBottom: '1rem' }}>
            <button className="tab-btn active">Posts</button>
            <button className="tab-btn">Saved</button>
            <button className="tab-btn">Collections</button>
          </div>
          
          {loading ? (
            <div>Loading posts...</div>
          ) : (
            <div style={{ display: 'flex', flexDirection: 'column', gap: '1.5rem' }}>
              {prompts.map(p => (
                <PromptCard key={p.id} prompt={p} currentUserId={currentUserId} />
              ))}
              {prompts.length === 0 && (
                <div style={{ textAlign: 'center', padding: '4rem', color: 'var(--text-muted)' }}>
                  This user hasn't posted any prompts yet.
                </div>
              )}
            </div>
          )}
        </div>

        <aside className="premium-card" style={{ padding: '1.5rem', height: 'fit-content' }}>
          <h3 style={{ marginBottom: '1.5rem' }}>About {profile.username}</h3>
          <div style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
            <div style={{ display: 'flex', alignItems: 'center', gap: '10px', color: 'var(--text-secondary)' }}>
              <Award size={18} /> Joined April 2024
            </div>
            {profile.college && (
              <div style={{ display: 'flex', alignItems: 'center', gap: '10px', color: 'var(--text-secondary)' }}>
                <User size={18} /> Student at {profile.college}
              </div>
            )}
          </div>
        </aside>
      </div>

      <style>{`
        .stat-item {
          display: flex;
          flex-direction: column;
          gap: 4px;
        }
        .stat-value {
          font-size: 1.25rem;
          font-weight: 700;
          color: var(--text-primary);
        }
        .stat-label {
          font-size: 0.8rem;
          color: var(--text-muted);
          text-transform: uppercase;
          letter-spacing: 1px;
        }
        .tab-btn {
          background: transparent;
          border: none;
          padding: 1rem 0;
          color: var(--text-muted);
          font-weight: 600;
          position: relative;
        }
        .tab-btn.active {
          color: var(--primary);
        }
        .tab-btn.active:after {
          content: "";
          position: absolute;
          bottom: -1px;
          left: 0;
          right: 0;
          height: 2px;
          background: var(--primary);
        }
      `}</style>
    </div>
  );
};

export default ProfilePage;

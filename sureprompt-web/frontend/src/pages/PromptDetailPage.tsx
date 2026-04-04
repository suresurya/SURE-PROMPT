import { useEffect, useState } from 'react'
import { useParams, Link } from 'react-router-dom'
import axios from 'axios'
import { Copy, Heart, Bookmark, Share2, Award, User, MessageSquare, Send, Check } from 'lucide-react'

const PromptDetailPage = ({ currentUserId }: { currentUserId?: number }) => {
  const { id } = useParams();
  const [prompt, setPrompt] = useState<any>(null);
  const [loading, setLoading] = useState(true);
  const [copied, setCopied] = useState(false);
  const [newComment, setNewComment] = useState('');

  useEffect(() => {
    setLoading(true);
    axios.get(`/api/prompts/${id}`)
      .then(res => setPrompt(res.data))
      .catch(err => console.error(err))
      .finally(() => setLoading(false));
  }, [id]);

  const handleCopy = () => {
    navigator.clipboard.writeText(prompt.promptBody);
    setCopied(true);
    setTimeout(() => setCopied(false), 2000);
  };

  if (loading) return <div style={{ textAlign: 'center', padding: '5rem' }}>Loading Prompt...</div>;
  if (!prompt) return <div style={{ textAlign: 'center', padding: '5rem' }}>Prompt not found.</div>;

  return (
    <div style={{ maxWidth: '1000px', margin: '0 auto', display: 'flex', flexDirection: 'column', gap: '2rem' }}>
      
      {/* Header Info */}
      <div className="premium-card" style={{ padding: '2rem' }}>
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'start', marginBottom: '1.5rem' }}>
          <div>
            <h1 style={{ fontSize: '2.2rem', marginBottom: '0.8rem' }}>{prompt.title}</h1>
            <div style={{ display: 'flex', gap: '1rem', alignItems: 'center' }}>
              <Link to={`/profile/${prompt.authorUsername}`} style={{ display: 'flex', gap: '8px', alignItems: 'center', textDecoration: 'none' }}>
                <div style={{ width: '32px', height: '32px', borderRadius: '50%', background: 'var(--bg-elevated)', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
                  {prompt.authorAvatar ? <img src={prompt.authorAvatar} style={{ width: '100%', height: '100%', borderRadius: '50%' }} /> : <User size={16} />}
                </div>
                <span style={{ color: 'var(--text-primary)', fontWeight: 600 }}>{prompt.authorName}</span>
              </Link>
              <span style={{ color: 'var(--text-muted)' }}>•</span>
              <span style={{ color: 'var(--text-muted)' }}>{new Date(prompt.createdAt).toLocaleDateString()}</span>
            </div>
          </div>
          
          <div style={{ display: 'flex', gap: '12px' }}>
            <button className="glass-btn action-btn-top"><Heart size={20} /></button>
            <button className="glass-btn action-btn-top"><Bookmark size={20} /></button>
            <button className="glass-btn action-btn-top"><Share2 size={20} /></button>
          </div>
        </div>

        <div style={{ display: 'flex', gap: '10px', flexWrap: 'wrap' }}>
          {prompt.tags?.map((tag: string) => (
            <span key={tag} className="detail-tag">#{tag}</span>
          ))}
          <span className="detail-badge">{prompt.difficulty}</span>
          <span className="detail-badge">{prompt.platform}</span>
        </div>
      </div>

      {/* Main Content Grid */}
      <div style={{ display: 'grid', gridTemplateColumns: '1.5fr 1fr', gap: '2rem' }}>
        
        {/* Left Column: Prompt and Output */}
        <div style={{ display: 'flex', flexDirection: 'column', gap: '2rem' }}>
          
          <section className="premium-card" style={{ padding: '2rem' }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '1.5rem' }}>
              <h2 style={{ fontSize: '1.25rem', color: 'var(--primary)' }}>Instruction Prompt</h2>
              <button 
                onClick={handleCopy}
                style={{ background: 'var(--bg-elevated)', border: '1px solid var(--border)', color: 'var(--text-primary)', display: 'flex', alignItems: 'center', gap: '8px' }}>
                {copied ? <><Check size={18} color="#10b981" /> Copied!</> : <><Copy size={18} /> Copy Prompt</>}
              </button>
            </div>
            <div className="code-block">
              {prompt.promptBody}
            </div>
          </section>

          <section className="premium-card" style={{ padding: '2rem', borderLeft: '4px solid var(--primary)' }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '1.5rem' }}>
              <h2 style={{ fontSize: '1.25rem', color: 'var(--primary)' }}>AI Output Result</h2>
              {prompt.aiVerified && (
                <div style={{ display: 'flex', alignItems: 'center', gap: '8px', color: '#10b981', background: 'rgba(16, 185, 129, 0.1)', padding: '4px 12px', borderRadius: '20px', fontSize: '0.9rem' }}>
                  <Award size={18} /> AI Verified
                </div>
              )}
            </div>
            <div className="code-block" style={{ background: 'rgba(255,255,255,0.02)', color: 'var(--text-secondary)' }}>
              {prompt.aiOutput}
            </div>
          </section>

          {/* Comments Section */}
          <section className="premium-card" style={{ padding: '2rem' }}>
            <h3 style={{ marginBottom: '1.5rem', display: 'flex', alignItems: 'center', gap: '10px' }}>
              <MessageSquare size={20} /> Comments ({prompt.commentCount || 0})
            </h3>
            
            <div style={{ display: 'flex', gap: '12px', marginBottom: '2rem' }}>
              <div className="avatar-placeholder" style={{ width: '40px', height: '40px' }}>Y</div>
              <div style={{ flex: 1, position: 'relative' }}>
                <textarea 
                  placeholder="Write a comment..." 
                  className="comment-input"
                  value={newComment}
                  onChange={(e) => setNewComment(e.target.value)}
                ></textarea>
                <button className="send-btn"><Send size={18} /></button>
              </div>
            </div>

            <div style={{ textAlign: 'center', padding: '2rem', color: 'var(--text-muted)' }}>
              No comments yet. Be the first to discuss!
            </div>
          </section>
        </div>

        {/* Right Column: AI Analysis */}
        <aside style={{ display: 'flex', flexDirection: 'column', gap: '2rem' }}>
          <div className="premium-card" style={{ padding: '2rem' }}>
            <h3 style={{ marginBottom: '1.2rem' }}>AI Verification Analysis</h3>
            {prompt.aiVerified ? (
              <div style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
                <div style={{ fontSize: '2.5rem', fontWeight: 800, color: 'var(--primary)' }}>
                  {prompt.aiScore}/10
                </div>
                <p style={{ color: 'var(--text-secondary)', fontSize: '0.95rem', lineHeight: '1.6' }}>
                  {prompt.aiVerificationReason || 'This prompt follows best practices for prompt engineering, including clear instructions and constraints.'}
                </p>
              </div>
            ) : (
              <div style={{ textAlign: 'center', padding: '1rem' }}>
                <p style={{ color: 'var(--text-muted)', marginBottom: '1rem' }}>AI evaluation not yet requested for this prompt.</p>
                <button style={{ width: '100%', background: 'var(--bg-elevated)' }}>Request Verification</button>
              </div>
            )}
          </div>
          
          <div className="premium-card" style={{ padding: '2rem' }}>
            <h3 style={{ marginBottom: '1.2rem' }}>Usage Stats</h3>
            <div style={{ display: 'flex', flexDirection: 'column', gap: '0.8rem' }}>
              <div className="stat-row"><span>Likes</span> <span>{prompt.likeCount}</span></div>
              <div className="stat-row"><span>Saves</span> <span>{prompt.saveCount}</span></div>
              <div className="stat-row"><span>Views</span> <span>{prompt.viewCount || 0}</span></div>
            </div>
          </div>
        </aside>
      </div>

      <style>{`
        .action-btn-top { background: transparent; padding: 10px; border-radius: 50%; color: var(--text-secondary); transition: all 0.2s; }
        .action-btn-top:hover { background: rgba(255,255,255,0.05); color: var(--text-primary); }
        .detail-tag { color: var(--primary); font-size: 0.9rem; font-weight: 500; }
        .detail-badge { background: var(--bg-elevated); color: var(--text-secondary); padding: 4px 12px; border-radius: 20px; font-size: 0.8rem; }
        .code-block { background: #000; border-radius: var(--radius-sm); padding: 1.5rem; font-family: 'Fira Code', monospace; font-size: 0.95rem; white-space: pre-wrap; word-break: break-word; border: 1px solid var(--border); }
        .comment-input { width: 100%; background: var(--bg-elevated); border: 1px solid var(--border); border-radius: var(--radius-md); padding: 1rem 3rem 1rem 1rem; color: white; resize: none; min-height: 80px; }
        .comment-input:focus { outline: none; border-color: var(--primary); }
        .send-btn { position: absolute; right: 10px; bottom: 10px; background: transparent; padding: 5px; color: var(--primary); }
        .stat-row { display: flex; justify-content: space-between; color: var(--text-secondary); font-size: 0.95rem; }
        .avatar-placeholder { background: var(--bg-elevated); border-radius: 50%; display: flex; alignItems: center; justifyContent: center; fontWeight: bold; font-size: 0.8rem; }
      `}</style>
    </div>
  );
};

export default PromptDetailPage;

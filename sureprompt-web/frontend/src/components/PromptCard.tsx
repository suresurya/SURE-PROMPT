import { Link } from 'react-router-dom'
import { Heart, MessageSquare, Bookmark, Eye, Award, ExternalLink } from 'lucide-react'
import { useState } from 'react'

interface PromptCardProps {
  prompt: any;
  currentUserId?: number;
}

const PromptCard = ({ prompt, currentUserId }: PromptCardProps) => {
  const [liked, setLiked] = useState(prompt.liked);
  const [likeCount, setLikeCount] = useState(prompt.likeCount || 0);

  const handleLike = (e: React.MouseEvent) => {
    e.preventDefault();
    setLiked(!liked);
    setLikeCount(liked ? likeCount - 1 : likeCount + 1);
    // Real API call here
  };

  return (
    <div className="premium-card" style={{ padding: '1.5rem', display: 'flex', flexDirection: 'column', gap: '1rem' }}>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'start' }}>
        <div style={{ display: 'flex', gap: '12px', alignItems: 'center' }}>
          {prompt.authorAvatar ? (
            <img src={prompt.authorAvatar} alt={prompt.authorUsername} style={{ width: '40px', height: '40px', borderRadius: '50%' }} />
          ) : (
            <div style={{ width: '40px', height: '40px', borderRadius: '50%', background: 'var(--bg-elevated)', display: 'flex', alignItems: 'center', justifyContent: 'center', fontWeight: 'bold' }}>
              {prompt.authorUsername.charAt(0).toUpperCase()}
            </div>
          )}
          <div>
            <Link to={`/profile/${prompt.authorUsername}`} style={{ color: 'var(--text-primary)', textDecoration: 'none', fontWeight: 600 }}>
              {prompt.authorName}
            </Link>
            <div style={{ fontSize: '0.8rem', color: 'var(--text-muted)' }}>{prompt.college || 'Developer'}</div>
          </div>
        </div>
        <div style={{ fontSize: '0.8rem', color: 'var(--text-muted)' }}>
          {new Date(prompt.createdAt).toLocaleDateString('en-US', { month: 'short', day: 'numeric' })}
        </div>
      </div>

      <div>
        <Link to={`/prompts/${prompt.id}`} style={{ textDecoration: 'none' }}>
          <h3 style={{ fontSize: '1.25rem', marginBottom: '0.5rem', color: 'var(--text-primary)' }}>{prompt.title}</h3>
        </Link>
        <div style={{ display: 'flex', gap: '8px', flexWrap: 'wrap' }}>
          {prompt.tags?.map((tag: string) => (
            <span key={tag} style={{ fontSize: '0.75rem', padding: '4px 10px', background: 'rgba(124, 58, 237, 0.1)', color: '#a78bfa', borderRadius: '20px' }}>
              #{tag}
            </span>
          ))}
          <span style={{ fontSize: '0.75rem', padding: '4px 10px', background: 'rgba(255, 255, 255, 0.05)', color: 'var(--text-secondary)', borderRadius: '20px' }}>
            {prompt.difficulty}
          </span>
        </div>
      </div>

      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', borderTop: '1px solid var(--border)', paddingTop: '1rem', marginTop: 'auto' }}>
        <div style={{ display: 'flex', gap: '1rem' }}>
          <button 
            onClick={handleLike}
            style={{ 
              background: 'transparent', 
              padding: 0, 
              display: 'flex', 
              alignItems: 'center', 
              gap: '6px', 
              color: liked ? 'var(--accent)' : 'var(--text-secondary)'
            }}
          >
            <Heart size={18} fill={liked ? 'currentColor' : 'none'} />
            <span style={{ fontSize: '0.9rem' }}>{likeCount}</span>
          </button>
          <div style={{ display: 'flex', alignItems: 'center', gap: '6px', color: 'var(--text-secondary)' }}>
            <MessageSquare size={18} />
            <span style={{ fontSize: '0.9rem' }}>{prompt.commentCount || 0}</span>
          </div>
        </div>
        <div style={{ display: 'flex', gap: '0.5rem' }}>
          {prompt.aiVerified && (
            <div title="AI Verified" style={{ color: '#10b981' }}>
              <Award size={18} />
            </div>
          )}
          <button style={{ background: 'transparent', padding: 0, color: 'var(--text-secondary)' }}>
            <Bookmark size={18} />
          </button>
        </div>
      </div>
    </div>
  );
};

export default PromptCard;

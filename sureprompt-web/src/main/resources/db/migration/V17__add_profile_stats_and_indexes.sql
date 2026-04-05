-- V17: Performance indexes for Profile + Stats system
-- Followers/Following lookup
CREATE INDEX IF NOT EXISTS idx_follows_follower ON follows(follower_id);
CREATE INDEX IF NOT EXISTS idx_follows_following ON follows(following_id);

-- Prompt ownership lookup
CREATE INDEX IF NOT EXISTS idx_prompts_user ON prompts(user_id);

-- AI score ranking (future leaderboard)
CREATE INDEX IF NOT EXISTS idx_prompts_ai_score ON prompts(ai_score DESC);

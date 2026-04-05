-- V22: AI Engine Async Hardening, Rate Limiting, and Cost Tracking

-- 1. Prompts Table Additions
ALTER TABLE prompts ADD COLUMN IF NOT EXISTS ai_status VARCHAR(20) DEFAULT 'PENDING';
ALTER TABLE prompts ADD COLUMN IF NOT EXISTS cost DOUBLE PRECISION DEFAULT 0.0;

-- 2. User API Key Additions (Rate Limiting)
ALTER TABLE user_api_keys ADD COLUMN IF NOT EXISTS daily_calls INT DEFAULT 0;
ALTER TABLE user_api_keys ADD COLUMN IF NOT EXISTS last_call_date DATE;

-- 3. Performance Indexes
-- Ensure fast sorting for the Explore feed and dynamic SQL ranking
CREATE INDEX IF NOT EXISTS idx_prompts_created_at_desc ON prompts(created_at DESC);

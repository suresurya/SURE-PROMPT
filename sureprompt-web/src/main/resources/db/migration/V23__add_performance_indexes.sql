-- V23__add_performance_indexes.sql
-- Description: Adds indexes required for optimizing core queries in production.

CREATE INDEX IF NOT EXISTS idx_prompts_created_at ON prompts(created_at DESC);
CREATE INDEX IF NOT EXISTS idx_prompts_ai_score ON prompts(ai_score DESC);
CREATE INDEX IF NOT EXISTS idx_users_username ON users(username);

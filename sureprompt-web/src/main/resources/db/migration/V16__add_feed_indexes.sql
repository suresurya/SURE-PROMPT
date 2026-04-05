-- Migration to add performance indexes for the interactive feed and trending ranking logic
CREATE INDEX IF NOT EXISTS idx_prompts_created_at ON prompts(created_at DESC);
CREATE INDEX IF NOT EXISTS idx_prompts_like_count ON prompts(like_count DESC);
CREATE INDEX IF NOT EXISTS idx_prompts_save_count ON prompts(save_count DESC);
CREATE INDEX IF NOT EXISTS idx_prompts_deleted ON prompts(deleted);

-- V21: Prompt Versioning, Reproduction Metadata, and Hybrid Scoring

-- 1. Prompt Entity Upgrades (Reproducibility & Quality)
ALTER TABLE prompts ADD COLUMN IF NOT EXISTS model_name VARCHAR(50);
ALTER TABLE prompts ADD COLUMN IF NOT EXISTS temperature FLOAT DEFAULT 0.7;
ALTER TABLE prompts ADD COLUMN IF NOT EXISTS tokens_used INT DEFAULT 0;
ALTER TABLE prompts ADD COLUMN IF NOT EXISTS community_score DOUBLE PRECISION DEFAULT 0.0;

-- 2. Prompt Versioning Table (Git-like history)
CREATE TABLE IF NOT EXISTS prompt_versions (
    id BIGSERIAL PRIMARY KEY,
    prompt_id BIGINT NOT NULL REFERENCES prompts(id) ON DELETE CASCADE,
    version INT NOT NULL,
    prompt_text TEXT NOT NULL,
    ai_output TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 3. Indexes for Versioning and Scoring
CREATE INDEX IF NOT EXISTS idx_prompt_versions_prompt_id ON prompt_versions(prompt_id);
CREATE INDEX IF NOT EXISTS idx_prompts_community_score ON prompts(community_score DESC);

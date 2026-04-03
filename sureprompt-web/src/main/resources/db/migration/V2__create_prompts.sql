CREATE TABLE prompts (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    title VARCHAR(200) NOT NULL,
    prompt_body TEXT NOT NULL,
    ai_output TEXT NOT NULL,
    difficulty VARCHAR(10),
    platform VARCHAR(50),
    like_count INTEGER DEFAULT 0,
    save_count INTEGER DEFAULT 0,
    ai_score DOUBLE PRECISION,
    ai_verified BOOLEAN DEFAULT FALSE,
    ai_verification_reason TEXT,
    pinned BOOLEAN DEFAULT FALSE,
    deleted BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE INDEX idx_prompts_user_id ON prompts(user_id);
CREATE INDEX idx_prompts_created_at ON prompts(created_at);
CREATE INDEX idx_prompts_like_count ON prompts(like_count);

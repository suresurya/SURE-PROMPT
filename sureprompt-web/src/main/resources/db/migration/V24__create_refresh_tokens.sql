-- V24__create_refresh_tokens.sql

CREATE TABLE refresh_tokens (
  id BIGSERIAL PRIMARY KEY,
  user_id BIGINT NOT NULL,
  token TEXT NOT NULL UNIQUE,
  is_revoked BOOLEAN DEFAULT FALSE,
  replaced_by_token TEXT,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  expiry TIMESTAMP NOT NULL,
  CONSTRAINT fk_rt_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);

CREATE INDEX idx_refresh_tokens_token ON refresh_tokens(token);

-- Final Optimization Index
CREATE INDEX idx_feed_combo ON prompts (created_at DESC, ai_score DESC);

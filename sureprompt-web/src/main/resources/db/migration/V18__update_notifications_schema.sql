-- V18: Update notification table safely without dropping
-- Add new columns safely
ALTER TABLE notifications ADD COLUMN IF NOT EXISTS actor_id BIGINT;
ALTER TABLE notifications ADD COLUMN IF NOT EXISTS prompt_id BIGINT;

-- Add constraints
ALTER TABLE notifications ADD CONSTRAINT fk_notifications_actor 
FOREIGN KEY (actor_id) REFERENCES users(id) ON DELETE CASCADE;

ALTER TABLE notifications ADD CONSTRAINT fk_notifications_prompt 
FOREIGN KEY (prompt_id) REFERENCES prompts(id) ON DELETE CASCADE;

-- Indexes for performance
-- (user_id, created_at DESC) for the dropdown list
CREATE INDEX IF NOT EXISTS idx_notifications_user_created ON notifications(user_id, created_at DESC);
-- (user_id, read) for the unread count badge
CREATE INDEX IF NOT EXISTS idx_notifications_unread ON notifications(user_id, read);

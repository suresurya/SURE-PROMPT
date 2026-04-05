-- V19: Upgrade reports system with target types, moderation states, and accountability
ALTER TABLE reports ADD COLUMN IF NOT EXISTS target_type VARCHAR(20); 
ALTER TABLE reports ADD COLUMN IF NOT EXISTS target_id BIGINT;
ALTER TABLE reports ADD COLUMN IF NOT EXISTS status VARCHAR(20) DEFAULT 'PENDING';
ALTER TABLE reports ADD COLUMN IF NOT EXISTS admin_note TEXT;
ALTER TABLE reports ADD COLUMN IF NOT EXISTS resolved_by BIGINT REFERENCES users(id);

-- DATA MIGRATION: Update legacy reports (V12)
-- Link prompt_id to target_id and set target_type to 'PROMPT' for old reports
UPDATE reports 
SET target_type = 'PROMPT', target_id = prompt_id 
WHERE target_type IS NULL OR target_id IS NULL;

-- Indexes for performance
CREATE INDEX IF NOT EXISTS idx_reports_status ON reports(status);
CREATE INDEX IF NOT EXISTS idx_reports_target ON reports(target_type, target_id);

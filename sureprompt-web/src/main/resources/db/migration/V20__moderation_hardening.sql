-- V20: Hardening moderation with constraints, audit logs, and performance indexes

-- 1. Data Cleanup (Ensure existing data respects the new constraints)
UPDATE reports SET status = 'PENDING' WHERE status IS NULL OR status NOT IN ('PENDING', 'RESOLVED', 'REJECTED');
UPDATE reports SET target_type = 'PROMPT' WHERE target_type IS NULL OR target_type NOT IN ('PROMPT', 'COMMENT', 'USER');

-- 2. Add ENUM-like Constraints
ALTER TABLE reports
ADD CONSTRAINT chk_reports_status
CHECK (status IN ('PENDING', 'RESOLVED', 'REJECTED'));

ALTER TABLE reports
ADD CONSTRAINT chk_reports_target
CHECK (target_type IN ('PROMPT', 'COMMENT', 'USER'));

-- 3. Prevent Duplicate Pending Reports (Spam Control)
CREATE UNIQUE INDEX IF NOT EXISTS uq_reports_unique
ON reports (reported_by, target_type, target_id)
WHERE status = 'PENDING';

-- 4. Audit Log Table
CREATE TABLE IF NOT EXISTS admin_actions (
    id BIGSERIAL PRIMARY KEY,
    admin_id BIGINT NOT NULL REFERENCES users(id),
    action VARCHAR(50) NOT NULL, -- e.g., 'BAN_USER', 'DELETE_PROMPT', 'REJECT_REPORT'
    target_type VARCHAR(20),      -- e.g., 'PROMPT', 'USER'
    target_id BIGINT,
    note TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 5. Performance Indexes for Dashboard
CREATE INDEX IF NOT EXISTS idx_prompts_deleted ON prompts(deleted);
CREATE INDEX IF NOT EXISTS idx_users_banned ON users(banned);

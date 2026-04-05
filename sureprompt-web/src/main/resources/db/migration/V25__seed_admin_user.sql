-- V25__seed_admin_user.sql
-- Seed the database with a default admin user for testing and initial setup

INSERT INTO users (email, username, display_name, role, password, created_at)
SELECT 'admin@sureprompt.com', 'admin', 'System Admin', 'ADMIN', '$2a$10$ByIovnizCQFADTKg4G07VOz2.u0G6TIn3m4t6gNn4M/XzI2.z9Y8G', CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM users WHERE username = 'admin');

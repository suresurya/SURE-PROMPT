-- V25__seed_admin_user.sql
-- Seed the database with a default admin user with a fresh BCrypt hash

DELETE FROM users WHERE username = 'admin';

INSERT INTO users (email, username, display_name, role, password, created_at)
VALUES ('admin@sureprompt.com', 'admin', 'System Admin', 'ADMIN', '$2a$10$OWfbI/p2RW9feGY3cag.LOpzmgfymvdTftKs36xbch0OQtPsuSAcC', CURRENT_TIMESTAMP);

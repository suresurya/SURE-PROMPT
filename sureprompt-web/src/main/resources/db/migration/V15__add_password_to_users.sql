-- Add password column to users table
ALTER TABLE users ADD COLUMN password VARCHAR(255);

-- Update existing users with default password 'admin123' (BCrypt encoded)
UPDATE users SET password = '$2a$10$ByIovnizCQFADTKg4G07VOz2.u0G6TIn3m4t6gNn4M/XzI2.z9Y8G' WHERE password IS NULL;

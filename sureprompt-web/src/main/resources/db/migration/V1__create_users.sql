CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    username VARCHAR(50) NOT NULL UNIQUE,
    display_name VARCHAR(100),
    college VARCHAR(150),
    bio VARCHAR(500),
    avatar_url VARCHAR(255),
    role VARCHAR(10) NOT NULL DEFAULT 'USER',
    streak_count INTEGER DEFAULT 0,
    banned BOOLEAN NOT NULL DEFAULT FALSE,
    oauth_provider VARCHAR(20),
    oauth_subject VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_username ON users(username);

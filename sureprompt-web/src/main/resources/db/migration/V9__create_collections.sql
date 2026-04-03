CREATE TABLE collections (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    name VARCHAR(100) NOT NULL,
    is_public BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE collection_prompts (
    collection_id BIGINT NOT NULL REFERENCES collections(id) ON DELETE CASCADE,
    prompt_id BIGINT NOT NULL REFERENCES prompts(id) ON DELETE CASCADE,
    PRIMARY KEY (collection_id, prompt_id)
);

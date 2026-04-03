CREATE TABLE prompt_tags (
    prompt_id BIGINT NOT NULL REFERENCES prompts(id) ON DELETE CASCADE,
    tag_id BIGINT NOT NULL REFERENCES tags(id) ON DELETE CASCADE,
    PRIMARY KEY (prompt_id, tag_id)
);

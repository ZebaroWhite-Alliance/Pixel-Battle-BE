CREATE TABLE template (
                          id SERIAL PRIMARY KEY,
                          name VARCHAR NOT NULL,
                          user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
                          pixels JSONB NOT NULL,
                          created_at TIMESTAMP DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS users (
    id                   BIGSERIAL PRIMARY KEY,
    username             VARCHAR(100) NOT NULL UNIQUE,
    password_hash        VARCHAR(255) NOT NULL,
    role                 VARCHAR(50)  NOT NULL DEFAULT 'USER',
    created_at           TIMESTAMP   NOT NULL,
    pixel_changes_count  INT         NOT NULL DEFAULT 0
    );
CREATE TABLE IF NOT EXISTS pixel_history (
     id         BIGSERIAL PRIMARY KEY,
     x          INT    NOT NULL,
     y          INT    NOT NULL,
     old_color  CHAR(7)  NOT NULL,
    new_color  CHAR(7)  NOT NULL,
    user_id    BIGINT  NOT NULL,
    changed_at TIMESTAMP NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
    );

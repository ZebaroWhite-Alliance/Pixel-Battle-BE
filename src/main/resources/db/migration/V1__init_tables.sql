CREATE TABLE IF NOT EXISTS users (
     id             BIGINT AUTO_INCREMENT PRIMARY KEY,
     username       VARCHAR(100) NOT NULL UNIQUE,
    password_hash  VARCHAR(255) NOT NULL,
    role           VARCHAR(50)  NOT NULL DEFAULT 'USER',
    created_at     TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP
    );

CREATE TABLE IF NOT EXISTS pixel_history (
     id            BIGINT AUTO_INCREMENT PRIMARY KEY,
     x             INT    NOT NULL,
     y             INT    NOT NULL,
     old_color     CHAR(7)  NOT NULL,
    new_color     CHAR(7)  NOT NULL,
    user_id       BIGINT  NOT NULL,
    changed_at    TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id)
    );

CREATE TABLE refresh_tokens (
    id         NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    token      VARCHAR2(200) NOT NULL UNIQUE,
    user_id    NUMBER        NOT NULL,
    expires_at TIMESTAMP     NOT NULL,
    is_revoked NUMBER(1)     DEFAULT 0 NOT NULL,
    created_at TIMESTAMP     NOT NULL,
    CONSTRAINT fk_refresh_tokens_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE INDEX idx_refresh_tokens_user_id ON refresh_tokens(user_id);
CREATE INDEX idx_refresh_tokens_token   ON refresh_tokens(token);

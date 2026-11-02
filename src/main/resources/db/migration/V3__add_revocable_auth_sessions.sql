CREATE TABLE auth_sessions (
    id UUID PRIMARY KEY,
    access_token_id UUID NOT NULL UNIQUE,
    refresh_token_hash VARCHAR(64) NOT NULL UNIQUE,
    user_id BIGINT NOT NULL REFERENCES user_tb1(id),
    refresh_expires_at TIMESTAMP WITH TIME ZONE NOT NULL,
    revoked_at TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE INDEX idx_auth_sessions_user_id ON auth_sessions(user_id);
CREATE INDEX idx_auth_sessions_expires_at ON auth_sessions(refresh_expires_at);

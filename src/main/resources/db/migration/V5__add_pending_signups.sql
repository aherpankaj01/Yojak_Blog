CREATE TABLE pending_signups (
                                 id UUID PRIMARY KEY,
                                 email VARCHAR(255) NOT NULL UNIQUE,
                                 name VARCHAR(255) NOT NULL,
                                 password_hash VARCHAR(255) NOT NULL,
                                 otp_hash VARCHAR(255) NOT NULL,
                                 expires_at TIMESTAMP NOT NULL,
                                 attempts INT NOT NULL DEFAULT 0,
                                 send_count INT NOT NULL DEFAULT 1,
                                 last_sent_at TIMESTAMP NOT NULL,
                                 created_at TIMESTAMP NOT NULL
);

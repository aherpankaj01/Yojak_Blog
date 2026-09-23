CREATE TABLE likes (
                       id UUID PRIMARY KEY,
                       post_slug VARCHAR(255) NOT NULL,
                       user_id UUID NOT NULL,
                       created_at TIMESTAMP NOT NULL,
                       CONSTRAINT fk_likes_post FOREIGN KEY (post_slug) REFERENCES posts(slug) ON DELETE CASCADE,
                       CONSTRAINT fk_likes_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
                       CONSTRAINT uq_likes_post_user UNIQUE (post_slug, user_id)
);

CREATE INDEX idx_likes_post_slug ON likes(post_slug);
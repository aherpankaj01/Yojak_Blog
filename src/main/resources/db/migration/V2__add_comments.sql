CREATE TABLE comments (
                          id UUID PRIMARY KEY,
                          content TEXT NOT NULL,
                          post_slug VARCHAR(255) NOT NULL,
                          user_id UUID NOT NULL,
                          created_at TIMESTAMP NOT NULL,
                          CONSTRAINT fk_comments_post FOREIGN KEY (post_slug) REFERENCES posts(slug) ON DELETE CASCADE,
                          CONSTRAINT fk_comments_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE INDEX idx_comments_post_slug ON comments(post_slug);
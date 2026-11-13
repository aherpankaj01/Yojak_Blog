CREATE TABLE bookmarks (
                           id UUID PRIMARY KEY,
                           post_slug VARCHAR(255) NOT NULL,
                           user_id UUID NOT NULL,
                           created_at TIMESTAMP NOT NULL,
                           CONSTRAINT fk_bookmarks_post FOREIGN KEY (post_slug) REFERENCES posts(slug) ON DELETE CASCADE,
                           CONSTRAINT fk_bookmarks_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
                           CONSTRAINT uq_bookmarks_post_user UNIQUE (post_slug, user_id)
);

CREATE INDEX idx_bookmarks_user_id ON bookmarks(user_id);
CREATE INDEX idx_bookmarks_post_slug ON bookmarks(post_slug);
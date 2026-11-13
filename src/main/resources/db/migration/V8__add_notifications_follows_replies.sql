
ALTER TABLE comments ADD COLUMN parent_id UUID;
ALTER TABLE comments
    ADD CONSTRAINT fk_comments_parent FOREIGN KEY (parent_id) REFERENCES comments(id) ON DELETE CASCADE;
CREATE INDEX idx_comments_parent_id ON comments(parent_id);


CREATE TABLE follows (
                         id UUID PRIMARY KEY,
                         follower_id UUID NOT NULL,
                         following_id UUID NOT NULL,
                         created_at TIMESTAMP NOT NULL,
                         CONSTRAINT fk_follows_follower FOREIGN KEY (follower_id) REFERENCES users(id) ON DELETE CASCADE,
                         CONSTRAINT fk_follows_following FOREIGN KEY (following_id) REFERENCES users(id) ON DELETE CASCADE,
                         CONSTRAINT uq_follows_pair UNIQUE (follower_id, following_id),
                         CONSTRAINT chk_follows_not_self CHECK (follower_id <> following_id)
);
CREATE INDEX idx_follows_follower ON follows(follower_id);
CREATE INDEX idx_follows_following ON follows(following_id);

CREATE TABLE notifications (
                               id UUID PRIMARY KEY,
                               recipient_id UUID NOT NULL,
                               actor_id UUID NOT NULL,
                               type VARCHAR(30) NOT NULL,
                               post_slug VARCHAR(255),
                               comment_id UUID,
                               is_read BOOLEAN NOT NULL DEFAULT FALSE,
                               created_at TIMESTAMP NOT NULL,
                               CONSTRAINT fk_notifications_recipient FOREIGN KEY (recipient_id) REFERENCES users(id) ON DELETE CASCADE,
                               CONSTRAINT fk_notifications_actor FOREIGN KEY (actor_id) REFERENCES users(id) ON DELETE CASCADE,
                               CONSTRAINT fk_notifications_post FOREIGN KEY (post_slug) REFERENCES posts(slug) ON DELETE CASCADE,
                               CONSTRAINT fk_notifications_comment FOREIGN KEY (comment_id) REFERENCES comments(id) ON DELETE CASCADE
);
CREATE INDEX idx_notifications_recipient_created ON notifications(recipient_id, created_at DESC);
CREATE INDEX idx_notifications_recipient_unread ON notifications(recipient_id, is_read);
CREATE TABLE users (
                       id UUID PRIMARY KEY,
                       name VARCHAR(255) NOT NULL,
                       email VARCHAR(255) NOT NULL UNIQUE,
                       password VARCHAR(255) NOT NULL,
                       created_at TIMESTAMP
);

CREATE TABLE posts (
                       slug VARCHAR(255) PRIMARY KEY,
                       title VARCHAR(255) NOT NULL,
                       content TEXT,
                       featured_image VARCHAR(255),
                       status VARCHAR(255) NOT NULL,
                       user_id UUID NOT NULL,
                       created_at TIMESTAMP,
                       updated_at TIMESTAMP,
                       CONSTRAINT fk_posts_user FOREIGN KEY (user_id) REFERENCES users(id)
);
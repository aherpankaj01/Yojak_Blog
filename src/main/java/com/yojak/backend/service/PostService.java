package com.yojak.backend.service;

import com.yojak.backend.dto.PostRequest;
import com.yojak.backend.dto.PostResponse;
import com.yojak.backend.entity.Post;
import com.yojak.backend.entity.User;
import com.yojak.backend.repository.CommentRepository;
import com.yojak.backend.repository.LikeRepository;
import com.yojak.backend.repository.PostRepository;
import com.yojak.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.UUID;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final LikeRepository likeRepository;
    private final CommentRepository commentRepository;

    public PostResponse createPost(PostRequest request, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (postRepository.existsBySlug(request.getSlug())) {
            throw new IllegalArgumentException("A post with this slug already exists");
        }

        Post post = new Post();
        post.setSlug(request.getSlug());
        post.setTitle(request.getTitle());
        post.setContent(request.getContent());
        post.setFeaturedImage(request.getFeaturedImage());
        post.setStatus(request.getStatus());
        post.setUser(user);

        Post saved = postRepository.save(post);
        return toResponse(saved);
    }

    public PostResponse updatePost(String slug, PostRequest request, String userEmail) {
        Post post = postRepository.findBySlug(slug)
                .orElseThrow(() -> new IllegalArgumentException("Post not found"));

        assertOwnership(post, userEmail);

        post.setTitle(request.getTitle());
        post.setContent(request.getContent());
        post.setFeaturedImage(request.getFeaturedImage());
        post.setStatus(request.getStatus());

        Post saved = postRepository.save(post);
        return toResponse(saved);
    }

    public void deletePost(String slug, String userEmail) {
        Post post = postRepository.findBySlug(slug)
                .orElseThrow(() -> new IllegalArgumentException("Post not found"));

        assertOwnership(post, userEmail);

        postRepository.delete(post);
    }

    public PostResponse getPost(String slug) {
        Post post = postRepository.findBySlug(slug)
                .orElseThrow(() -> new IllegalArgumentException("Post not found"));
        return toResponse(post);
    }

    public List<PostResponse> getPosts(String status, String sortBy) {
        List<Post> posts;

        if (status == null) {
            posts = postRepository.findAll();
        } else if ("most_liked".equals(sortBy)) {
            posts = postRepository.findByStatusOrderByLikeCountDesc(status);
        } else if ("most_commented".equals(sortBy)) {
            posts = postRepository.findByStatusOrderByCommentCountDesc(status);
        } else {
            posts = postRepository.findByStatusOrderByCreatedAtDesc(status);
        }

        return posts.stream().map(this::toResponse).toList();
    }

    private void assertOwnership(Post post, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (!post.getUser().getId().equals(user.getId())) {
            throw new SecurityException("You are not authorized to modify this post");
        }
    }

    public List<PostResponse> getPostsByUser(UUID userId, String sortBy) {
        List<Post> posts;

        if ("most_liked".equals(sortBy)) {
            posts = postRepository.findByUserIdOrderByLikeCountDesc(userId);
        } else if ("most_commented".equals(sortBy)) {
            posts = postRepository.findByUserIdOrderByCommentCountDesc(userId);
        } else {
            posts = postRepository.findByUserIdOrderByCreatedAtDesc(userId);
        }

        return posts.stream().map(this::toResponse).toList();
    }

    private PostResponse toResponse(Post post) {
        long likeCount = likeRepository.countByPostSlug(post.getSlug());
        long commentCount = commentRepository.countByPostSlug(post.getSlug());

        return new PostResponse(
                post.getSlug(),
                post.getTitle(),
                post.getContent(),
                post.getFeaturedImage(),
                post.getStatus(),
                post.getUser().getId(),
                post.getCreatedAt(),
                post.getUpdatedAt(),
                likeCount,
                commentCount
        );
    }
}
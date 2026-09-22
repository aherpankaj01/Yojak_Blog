package com.yojak.backend.service;

import com.yojak.backend.dto.PostRequest;
import com.yojak.backend.dto.PostResponse;
import com.yojak.backend.entity.Post;
import com.yojak.backend.entity.User;
import com.yojak.backend.repository.PostRepository;
import com.yojak.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;

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
        // slug itself is intentionally NOT updated here — it's the primary key.
        // Renaming a slug would require a delete+recreate, not a field update.

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

    public List<PostResponse> getPosts(String status) {
        List<Post> posts = (status != null)
                ? postRepository.findByStatus(status)
                : postRepository.findAll();

        return posts.stream().map(this::toResponse).toList();
    }

    // --- private helpers ---

    private void assertOwnership(Post post, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (!post.getUser().getId().equals(user.getId())) {
            throw new SecurityException("You are not authorized to modify this post");
        }
    }

    private PostResponse toResponse(Post post) {
        return new PostResponse(
                post.getSlug(),
                post.getTitle(),
                post.getContent(),
                post.getFeaturedImage(),
                post.getStatus(),
                post.getUser().getId(),
                post.getCreatedAt(),
                post.getUpdatedAt()
        );
    }
}
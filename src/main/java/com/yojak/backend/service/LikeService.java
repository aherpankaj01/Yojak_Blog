package com.yojak.backend.service;

import com.yojak.backend.dto.LikeResponse;
import com.yojak.backend.entity.Like;
import com.yojak.backend.entity.Post;
import com.yojak.backend.entity.User;
import com.yojak.backend.repository.LikeRepository;
import com.yojak.backend.repository.PostRepository;
import com.yojak.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LikeService {

    private final LikeRepository likeRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public LikeResponse likePost(String postSlug, String userEmail) {
        Post post = postRepository.findBySlug(postSlug)
                .orElseThrow(() -> new IllegalArgumentException("Post not found"));

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (!likeRepository.existsByPostSlugAndUserId(postSlug, user.getId())) {
            Like like = new Like();
            like.setPost(post);
            like.setUser(user);
            likeRepository.save(like);
        }

        return getLikeStatus(postSlug, userEmail);
    }

    public LikeResponse unlikePost(String postSlug, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        likeRepository.findByPostSlugAndUserId(postSlug, user.getId())
                .ifPresent(likeRepository::delete);

        return getLikeStatus(postSlug, userEmail);
    }

    public LikeResponse getLikeStatus(String postSlug, String userEmail) {
        long count = likeRepository.countByPostSlug(postSlug);

        boolean liked = false;
        if (userEmail != null) {
            liked = userRepository.findByEmail(userEmail)
                    .map(u -> likeRepository.existsByPostSlugAndUserId(postSlug, u.getId()))
                    .orElse(false);
        }

        return new LikeResponse(count, liked);
    }
}
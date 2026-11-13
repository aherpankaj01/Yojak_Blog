package com.yojak.backend.service;

import com.yojak.backend.dto.BookmarkResponse;
import com.yojak.backend.dto.PostResponse;
import com.yojak.backend.entity.Bookmark;
import com.yojak.backend.entity.Post;
import com.yojak.backend.entity.User;
import com.yojak.backend.repository.BookmarkRepository;
import com.yojak.backend.repository.PostRepository;
import com.yojak.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookmarkService {

    private final BookmarkRepository bookmarkRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final PostService postService;

    public BookmarkResponse bookmarkPost(String postSlug, String userEmail) {
        Post post = postRepository.findBySlug(postSlug)
                .orElseThrow(() -> new IllegalArgumentException("Post not found"));
        User user = findUser(userEmail);

        if (!"active".equals(post.getStatus()) && !post.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Post not found");
        }

        // Duplicate prevention: app-level check, backed by the DB unique constraint.
        if (!bookmarkRepository.existsByPostSlugAndUserId(postSlug, user.getId())) {
            try {
                Bookmark bookmark = new Bookmark();
                bookmark.setPost(post);
                bookmark.setUser(user);
                bookmarkRepository.save(bookmark);
            } catch (DataIntegrityViolationException ignored) {
                // A concurrent request already saved it - the end state is the same.
            }
        }
        return new BookmarkResponse(true);
    }

    public BookmarkResponse unbookmarkPost(String postSlug, String userEmail) {
        User user = findUser(userEmail);
        bookmarkRepository.findByPostSlugAndUserId(postSlug, user.getId())
                .ifPresent(bookmarkRepository::delete);
        return new BookmarkResponse(false);
    }

    public BookmarkResponse getStatus(String postSlug, String userEmail) {
        User user = findUser(userEmail);
        return new BookmarkResponse(
                bookmarkRepository.existsByPostSlugAndUserId(postSlug, user.getId()));
    }

    public List<PostResponse> getSavedPosts(String userEmail) {
        User user = findUser(userEmail);
        return bookmarkRepository.findSavedPosts(user.getId())
                .stream()
                .map(postService::toResponse)
                .toList();
    }

    private User findUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }
}
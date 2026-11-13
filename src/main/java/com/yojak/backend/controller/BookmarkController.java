package com.yojak.backend.controller;

import com.yojak.backend.dto.BookmarkResponse;
import com.yojak.backend.dto.PostResponse;
import com.yojak.backend.service.BookmarkService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class BookmarkController {

    private final BookmarkService bookmarkService;

    @PostMapping("/api/posts/{slug}/bookmark")
    public ResponseEntity<BookmarkResponse> bookmark(@PathVariable String slug, Authentication authentication) {
        return ResponseEntity.ok(bookmarkService.bookmarkPost(slug, authentication.getName()));
    }

    @DeleteMapping("/api/posts/{slug}/bookmark")
    public ResponseEntity<BookmarkResponse> unbookmark(@PathVariable String slug, Authentication authentication) {
        return ResponseEntity.ok(bookmarkService.unbookmarkPost(slug, authentication.getName()));
    }

    @GetMapping("/api/posts/{slug}/bookmark")
    public ResponseEntity<BookmarkResponse> status(@PathVariable String slug, Authentication authentication) {
        return ResponseEntity.ok(bookmarkService.getStatus(slug, authentication.getName()));
    }

    @GetMapping("/api/bookmarks")
    public ResponseEntity<List<PostResponse>> savedPosts(Authentication authentication) {
        return ResponseEntity.ok(bookmarkService.getSavedPosts(authentication.getName()));
    }
}
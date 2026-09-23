package com.yojak.backend.controller;

import com.yojak.backend.dto.PostRequest;
import com.yojak.backend.dto.PostResponse;
import com.yojak.backend.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @PostMapping
    public ResponseEntity<PostResponse> createPost(@Valid @RequestBody PostRequest request,
                                                   Authentication authentication) {
        String userEmail = authentication.getName();
        return ResponseEntity.ok(postService.createPost(request, userEmail));
    }

    @PutMapping("/{slug}")
    public ResponseEntity<PostResponse> updatePost(@PathVariable String slug,
                                                   @Valid @RequestBody PostRequest request,
                                                   Authentication authentication) {
        String userEmail = authentication.getName();
        return ResponseEntity.ok(postService.updatePost(slug, request, userEmail));
    }

    @DeleteMapping("/{slug}")
    public ResponseEntity<Void> deletePost(@PathVariable String slug,
                                           Authentication authentication) {
        String userEmail = authentication.getName();
        postService.deletePost(slug, userEmail);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{slug}")
    public ResponseEntity<PostResponse> getPost(@PathVariable String slug) {
        return ResponseEntity.ok(postService.getPost(slug));
    }

    @GetMapping
    public ResponseEntity<List<PostResponse>> getPosts(
            @RequestParam(required = false) String status,
            @RequestParam(required = false, defaultValue = "recent") String sortBy) {
        return ResponseEntity.ok(postService.getPosts(status, sortBy));
    }

}
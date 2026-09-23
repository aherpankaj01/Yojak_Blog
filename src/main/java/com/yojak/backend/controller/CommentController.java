package com.yojak.backend.controller;

import com.yojak.backend.dto.CommentRequest;
import com.yojak.backend.dto.CommentResponse;
import com.yojak.backend.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/api/posts/{slug}/comments")
    public ResponseEntity<CommentResponse> addComment(@PathVariable String slug,
                                                      @Valid @RequestBody CommentRequest request,
                                                      Authentication authentication) {
        String userEmail = authentication.getName();
        return ResponseEntity.ok(commentService.addComment(slug, request, userEmail));
    }

    @GetMapping("/api/posts/{slug}/comments")
    public ResponseEntity<List<CommentResponse>> getComments(@PathVariable String slug) {
        return ResponseEntity.ok(commentService.getComments(slug));
    }

    @DeleteMapping("/api/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable UUID commentId,
                                              Authentication authentication) {
        String userEmail = authentication.getName();
        commentService.deleteComment(commentId, userEmail);
        return ResponseEntity.noContent().build();
    }
}
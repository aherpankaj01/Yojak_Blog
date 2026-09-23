package com.yojak.backend.controller;

import com.yojak.backend.dto.LikeResponse;
import com.yojak.backend.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/posts/{slug}/likes")
@RequiredArgsConstructor
public class LikeController {

    private final LikeService likeService;

    @PostMapping
    public ResponseEntity<LikeResponse> like(@PathVariable String slug, Authentication authentication) {
        String userEmail = authentication.getName();
        return ResponseEntity.ok(likeService.likePost(slug, userEmail));
    }

    @DeleteMapping
    public ResponseEntity<LikeResponse> unlike(@PathVariable String slug, Authentication authentication) {
        String userEmail = authentication.getName();
        return ResponseEntity.ok(likeService.unlikePost(slug, userEmail));
    }

    @GetMapping
    public ResponseEntity<LikeResponse> getStatus(@PathVariable String slug, Authentication authentication) {
        String userEmail = (authentication != null) ? authentication.getName() : null;
        return ResponseEntity.ok(likeService.getLikeStatus(slug, userEmail));
    }
}
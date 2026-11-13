package com.yojak.backend.controller;

import com.yojak.backend.dto.FollowStatusResponse;
import com.yojak.backend.service.FollowService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/users/{userId}")
@RequiredArgsConstructor
public class FollowController {

    private final FollowService followService;

    @PostMapping("/follow")
    public ResponseEntity<FollowStatusResponse> follow(@PathVariable UUID userId, Authentication authentication) {
        return ResponseEntity.ok(followService.follow(userId, authentication.getName()));
    }

    @DeleteMapping("/follow")
    public ResponseEntity<FollowStatusResponse> unfollow(@PathVariable UUID userId, Authentication authentication) {
        return ResponseEntity.ok(followService.unfollow(userId, authentication.getName()));
    }

    @GetMapping("/follow-status")
    public ResponseEntity<FollowStatusResponse> status(@PathVariable UUID userId, Authentication authentication) {
        String email = (authentication != null) ? authentication.getName() : null;
        return ResponseEntity.ok(followService.getStatus(userId, email));
    }
}
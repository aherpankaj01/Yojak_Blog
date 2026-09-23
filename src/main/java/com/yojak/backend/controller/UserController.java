package com.yojak.backend.controller;

import com.yojak.backend.dto.PostResponse;
import com.yojak.backend.dto.UpdateProfileRequest;
import com.yojak.backend.dto.UserProfileResponse;
import com.yojak.backend.service.PostService;
import com.yojak.backend.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final PostService postService;

    @GetMapping("/me")
    public ResponseEntity<UserProfileResponse> getOwnProfile(Authentication authentication) {
        String userEmail = authentication.getName();
        return ResponseEntity.ok(userService.getOwnProfile(userEmail));
    }

    @PutMapping("/me")
    public ResponseEntity<UserProfileResponse> updateProfile(@Valid @RequestBody UpdateProfileRequest request,
                                                             Authentication authentication) {
        String userEmail = authentication.getName();
        return ResponseEntity.ok(userService.updateProfile(request, userEmail));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserProfileResponse> getPublicProfile(@PathVariable UUID userId) {
        return ResponseEntity.ok(userService.getProfile(userId));
    }

    @GetMapping("/{userId}/posts")
    public ResponseEntity<List<PostResponse>> getPostsByUser(
            @PathVariable UUID userId,
            @RequestParam(required = false, defaultValue = "recent") String sortBy) {
        return ResponseEntity.ok(postService.getPostsByUser(userId, sortBy));
    }
}
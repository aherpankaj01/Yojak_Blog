package com.yojak.backend.service;

import com.yojak.backend.dto.UpdateProfileRequest;
import com.yojak.backend.dto.UserProfileResponse;
import com.yojak.backend.entity.User;
import com.yojak.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;
import com.yojak.backend.dto.UserSummaryResponse;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public UserProfileResponse getProfile(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return toResponse(user);
    }

    public UserProfileResponse getOwnProfile(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return toResponse(user);
    }

    public UserProfileResponse updateProfile(UpdateProfileRequest request, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        user.setName(request.getName());
        user.setBio(request.getBio());
        user.setAvatar(request.getAvatar());

        User saved = userRepository.save(user);
        return toResponse(saved);
    }

    public List<UserSummaryResponse> searchUsers(String query) {
        List<User> found = new ArrayList<>();

        if (query.length() == 36) {
            try {
                userRepository.findById(UUID.fromString(query)).ifPresent(found::add);
            } catch (IllegalArgumentException ignored) {
            }
        }

        for (User u : userRepository.findTop10ByNameContainingIgnoreCase(query)) {
            boolean already = found.stream().anyMatch(f -> f.getId().equals(u.getId()));
            if (!already) found.add(u);
        }

        return found.stream()
                .map(u -> new UserSummaryResponse(u.getId(), u.getName(), u.getBio(), u.getAvatar()))
                .toList();
    }


    private UserProfileResponse toResponse(User user) {
        return new UserProfileResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getBio(),
                user.getAvatar()
        );
    }
}
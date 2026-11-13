package com.yojak.backend.service;

import com.yojak.backend.dto.FollowStatusResponse;
import com.yojak.backend.entity.Follow;
import com.yojak.backend.entity.User;
import com.yojak.backend.repository.FollowRepository;
import com.yojak.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FollowService {

    private final FollowRepository followRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    @Transactional
    public FollowStatusResponse follow(UUID targetId, String userEmail) {
        User me = getByEmail(userEmail);
        User target = userRepository.findById(targetId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (me.getId().equals(target.getId())) {
            throw new IllegalArgumentException("You cannot follow yourself");
        }

        if (!followRepository.existsByFollowerIdAndFollowingId(me.getId(), target.getId())) {
            Follow follow = new Follow();
            follow.setFollower(me);
            follow.setFollowing(target);
            followRepository.save(follow);
            notificationService.notifyFollow(target, me);
        }

        return getStatus(targetId, userEmail);
    }

    @Transactional
    public FollowStatusResponse unfollow(UUID targetId, String userEmail) {
        User me = getByEmail(userEmail);
        User target = userRepository.findById(targetId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        followRepository.findByFollowerIdAndFollowingId(me.getId(), target.getId())
                .ifPresent(f -> {
                    followRepository.delete(f);
                    notificationService.removeFollow(target, me);
                });

        return getStatus(targetId, userEmail);
    }

    @Transactional(readOnly = true)
    public FollowStatusResponse getStatus(UUID targetId, String userEmail) {
        long followers = followRepository.countByFollowingId(targetId);
        long following = followRepository.countByFollowerId(targetId);

        boolean isFollowing = false;
        if (userEmail != null) {
            isFollowing = userRepository.findByEmail(userEmail)
                    .map(u -> followRepository.existsByFollowerIdAndFollowingId(u.getId(), targetId))
                    .orElse(false);
        }

        return new FollowStatusResponse(followers, following, isFollowing);
    }

    private User getByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }
}
package com.yojak.backend.service;

import com.yojak.backend.dto.ResetPasswordRequest;
import com.yojak.backend.entity.PasswordResetToken;
import com.yojak.backend.entity.User;
import com.yojak.backend.repository.PasswordResetTokenRepository;
import com.yojak.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HexFormat;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PasswordResetService {

    private static final Logger log = LoggerFactory.getLogger(PasswordResetService.class);

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    @Value("${app.password-reset.expiry-minutes}")
    private long expiryMinutes;

    @Value("${app.password-reset.cooldown-seconds}")
    private long cooldownSeconds;

    @Value("${app.frontend-url}")
    private String frontendUrl;

    private final SecureRandom random = new SecureRandom();

    @Transactional
    public void requestReset(String rawEmail) {
        String email = rawEmail.trim();

        userRepository.findByEmail(email).ifPresent(user -> {
            LocalDateTime now = LocalDateTime.now();

            Optional<PasswordResetToken> latest =
                    tokenRepository.findFirstByUserIdOrderByCreatedAtDesc(user.getId());
            if (latest.isPresent()
                    && Duration.between(latest.get().getCreatedAt(), now).getSeconds() < cooldownSeconds) {
                return;
            }

            tokenRepository.deleteByUserId(user.getId());

            byte[] bytes = new byte[32];
            random.nextBytes(bytes);
            String rawToken = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);

            PasswordResetToken token = new PasswordResetToken();
            token.setUserId(user.getId());
            token.setTokenHash(hash(rawToken));
            token.setExpiresAt(now.plusMinutes(expiryMinutes));
            tokenRepository.save(token);

            String link = frontendUrl + "/reset-password?token=" + rawToken;
            try {
                emailService.sendPasswordReset(user.getEmail(), link, expiryMinutes);
            } catch (RuntimeException e) {
                log.error("Could not send password reset email", e);
            }
        });
    }

    @Transactional
    public void reset(ResetPasswordRequest request) {
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new IllegalArgumentException("Passwords do not match.");
        }

        PasswordResetToken token = tokenRepository.findByTokenHash(hash(request.getToken().trim()))
                .orElseThrow(() -> new IllegalArgumentException(
                        "This reset link is invalid or has already been used."));

        if (LocalDateTime.now().isAfter(token.getExpiresAt())) {
            throw new IllegalArgumentException("This reset link has expired. Please request a new one.");
        }

        User user = userRepository.findById(token.getUserId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "This reset link is invalid or has already been used."));

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        tokenRepository.deleteByUserId(user.getId());
    }

    @Scheduled(fixedRate = 3_600_000)
    @Transactional
    public void purgeExpired() {
        tokenRepository.deleteByExpiresAtBefore(LocalDateTime.now());
    }

    private String hash(String rawToken) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(digest.digest(rawToken.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 not available", e);
        }
    }
}
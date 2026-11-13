package com.yojak.backend.service;

import com.yojak.backend.dto.*;
import com.yojak.backend.entity.PendingSignup;
import com.yojak.backend.entity.User;
import com.yojak.backend.repository.PendingSignupRepository;
import com.yojak.backend.repository.UserRepository;
import com.yojak.backend.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class SignupVerificationService {

    private final UserRepository userRepository;
    private final PendingSignupRepository pendingRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final JwtUtil jwtUtil;

    @Value("${app.otp.expiry-minutes}")
    private long expiryMinutes;

    @Value("${app.otp.resend-cooldown-seconds}")
    private long cooldownSeconds;

    @Value("${app.otp.max-attempts}")
    private int maxAttempts;

    @Value("${app.otp.max-sends}")
    private int maxSends;

    private final SecureRandom random = new SecureRandom();

    @Transactional
    public SignupPendingResponse startSignup(SignupRequest request) {
        String email = request.getEmail().trim();

        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email already registered");
        }

        PendingSignup pending = pendingRepository.findByEmail(email).orElseGet(() -> {
            PendingSignup p = new PendingSignup();
            p.setEmail(email);
            return p;
        });

        pending.setName(request.getName().trim());
        pending.setPasswordHash(passwordEncoder.encode(request.getPassword()));

        String otp = issueOtp(pending, LocalDateTime.now());
        pendingRepository.save(pending);
        emailService.sendOtp(email, otp, expiryMinutes);

        return response(email);
    }

    @Transactional(noRollbackFor = IllegalArgumentException.class)
    public AuthResponse verify(VerifyOtpRequest request) {
        String email = request.getEmail().trim();

        PendingSignup pending = pendingRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("No pending signup found. Please sign up again."));

        if (LocalDateTime.now().isAfter(pending.getExpiresAt())) {
            throw new IllegalArgumentException("This code has expired. Please request a new one.");
        }

        if (pending.getAttempts() >= maxAttempts) {
            throw new IllegalArgumentException("Too many incorrect attempts. Please request a new code.");
        }

        if (!passwordEncoder.matches(request.getOtp().trim(), pending.getOtpHash())) {
            pending.setAttempts(pending.getAttempts() + 1);
            pendingRepository.save(pending);
            int left = maxAttempts - pending.getAttempts();
            throw new IllegalArgumentException(left > 0
                    ? "Incorrect code. " + left + " attempt(s) left."
                    : "Too many incorrect attempts. Please request a new code.");
        }

        if (userRepository.existsByEmail(email)) {
            pendingRepository.delete(pending);
            throw new IllegalArgumentException("Email already registered");
        }

        User user = new User();
        user.setName(pending.getName());
        user.setEmail(pending.getEmail());
        user.setPassword(pending.getPasswordHash());
        User saved = userRepository.save(user);

        pendingRepository.delete(pending);

        String token = jwtUtil.generateToken(saved.getEmail());
        return new AuthResponse(token, saved.getId(), saved.getName(), saved.getEmail());
    }

    @Transactional
    public SignupPendingResponse resend(ResendOtpRequest request) {
        String email = request.getEmail().trim();

        PendingSignup pending = pendingRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("No pending signup found. Please sign up again."));

        String otp = issueOtp(pending, LocalDateTime.now());
        pendingRepository.save(pending);
        emailService.sendOtp(email, otp, expiryMinutes);

        return response(email);
    }

    @Scheduled(fixedRate = 3_600_000)
    @Transactional
    public void purgeOldPending() {
        pendingRepository.deleteByExpiresAtBefore(LocalDateTime.now().minusHours(1));
    }

    private String issueOtp(PendingSignup p, LocalDateTime now) {
        if (p.getExpiresAt() != null && now.isAfter(p.getExpiresAt())) {
            p.setSendCount(0);
        }

        if (p.getLastSentAt() != null) {
            long since = Duration.between(p.getLastSentAt(), now).getSeconds();
            if (since < cooldownSeconds) {
                throw new IllegalArgumentException(
                        "Please wait " + (cooldownSeconds - since) + " seconds before requesting another code.");
            }
        }

        if (p.getSendCount() >= maxSends) {
            throw new IllegalArgumentException("Too many codes requested. Please try again in a few minutes.");
        }

        String otp = String.format("%06d", random.nextInt(1_000_000));
        p.setOtpHash(passwordEncoder.encode(otp));
        p.setExpiresAt(now.plusMinutes(expiryMinutes));
        p.setLastSentAt(now);
        p.setAttempts(0);
        p.setSendCount(p.getSendCount() + 1);
        return otp;
    }

    private SignupPendingResponse response(String email) {
        return new SignupPendingResponse(
                email,
                "We sent a 6-digit code to your email.",
                expiryMinutes * 60,
                cooldownSeconds
        );
    }
}
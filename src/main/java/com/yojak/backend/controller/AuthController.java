package com.yojak.backend.controller;

import com.yojak.backend.dto.*;
import com.yojak.backend.service.AuthService;
import com.yojak.backend.service.SignupVerificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import com.yojak.backend.service.PasswordResetService;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final SignupVerificationService signupVerificationService;
    private final PasswordResetService passwordResetService;

    @PostMapping("/signup")
    public ResponseEntity<SignupPendingResponse> signup(@Valid @RequestBody SignupRequest request) {
        return ResponseEntity.ok(signupVerificationService.startSignup(request));
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<AuthResponse> verifyOtp(@Valid @RequestBody VerifyOtpRequest request) {
        return ResponseEntity.ok(signupVerificationService.verify(request));
    }

    @PostMapping("/resend-otp")
    public ResponseEntity<SignupPendingResponse> resendOtp(@Valid @RequestBody ResendOtpRequest request) {
        return ResponseEntity.ok(signupVerificationService.resend(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @GetMapping("/me")
    public ResponseEntity<AuthResponse> getCurrentUser(Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(authService.getCurrentUser(authentication.getName()));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<Map<String, String>> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        passwordResetService.requestReset(request.getEmail());
        return ResponseEntity.ok(Map.of("message",
                "If an account exists for that email, we've sent a password reset link."));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Map<String, String>> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        passwordResetService.reset(request);
        return ResponseEntity.ok(Map.of("message", "Your password has been updated. You can now sign in."));
    }
}
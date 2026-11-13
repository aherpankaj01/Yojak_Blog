package com.yojak.backend.service;

import com.yojak.backend.exception.EmailSendException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.List;
import java.util.Map;

@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    @Value("${brevo.api-key}")
    private String apiKey;

    @Value("${brevo.sender-email}")
    private String senderEmail;

    @Value("${brevo.sender-name}")
    private String senderName;

    private final RestClient restClient = RestClient.create();

    public void sendOtp(String toEmail, String otp, long expiryMinutes) {
        if (isDevMode()) {
            log.warn("BREVO_API_KEY not set. DEV MODE: OTP for {} is {}", toEmail, otp);
            return;
        }

        String html = "<div style=\"font-family:Arial,sans-serif\">"
                + "<h2>Verify your email</h2>"
                + "<p>Your Yojak verification code is:</p>"
                + "<p style=\"font-size:28px;letter-spacing:6px;font-weight:bold\">" + otp + "</p>"
                + "<p>This code expires in " + expiryMinutes + " minutes. "
                + "If you didn't try to sign up, you can ignore this email.</p></div>";

        send(toEmail, "Your Yojak verification code", html);
    }

    public void sendPasswordReset(String toEmail, String link, long expiryMinutes) {
        if (isDevMode()) {
            log.warn("BREVO_API_KEY not set. DEV MODE: password reset link for {} is {}", toEmail, link);
            return;
        }

        String html = "<div style=\"font-family:Arial,sans-serif\">"
                + "<h2>Reset your password</h2>"
                + "<p>We received a request to reset your Yojak password. Click the button below to choose a new one.</p>"
                + "<p><a href=\"" + link + "\" style=\"display:inline-block;padding:12px 20px;"
                + "background:#6366f1;color:#ffffff;text-decoration:none;border-radius:6px\">Reset password</a></p>"
                + "<p>This link expires in " + expiryMinutes + " minutes and can be used once. "
                + "If you didn't request this, you can safely ignore this email. Your password won't change.</p></div>";

        send(toEmail, "Reset your Yojak password", html);
    }

    private boolean isDevMode() {
        return apiKey == null || apiKey.isBlank();
    }

    private void send(String toEmail, String subject, String html) {
        Map<String, Object> body = Map.of(
                "sender", Map.of("name", senderName, "email", senderEmail),
                "to", List.of(Map.of("email", toEmail)),
                "subject", subject,
                "htmlContent", html
        );

        try {
            restClient.post()
                    .uri("https://api.brevo.com/v3/smtp/email")
                    .header("api-key", apiKey)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(body)
                    .retrieve()
                    .toBodilessEntity();
        } catch (RestClientException e) {
            log.error("Failed to send email", e);
            throw new EmailSendException("We couldn't send the email. Please try again.");
        }
    }
}
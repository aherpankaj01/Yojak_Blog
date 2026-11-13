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
        if (apiKey == null || apiKey.isBlank()) {
            log.warn("BREVO_API_KEY not set. DEV MODE: OTP for {} is {}", toEmail, otp);
            return;
        }

        String html = "<div style=\"font-family:Arial,sans-serif\">"
                + "<h2>Verify your email</h2>"
                + "<p>Your Yojak verification code is:</p>"
                + "<p style=\"font-size:28px;letter-spacing:6px;font-weight:bold\">" + otp + "</p>"
                + "<p>This code expires in " + expiryMinutes + " minutes. "
                + "If you didn't try to sign up, you can ignore this email.</p></div>";

        Map<String, Object> body = Map.of(
                "sender", Map.of("name", senderName, "email", senderEmail),
                "to", List.of(Map.of("email", toEmail)),
                "subject", "Your Yojak verification code",
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
            log.error("Failed to send OTP email", e);
            throw new EmailSendException("We couldn't send the verification email. Please try again.");
        }
    }
}
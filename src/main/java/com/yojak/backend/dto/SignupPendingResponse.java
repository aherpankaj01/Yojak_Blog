package com.yojak.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class SignupPendingResponse {
    private String email;
    private String message;
    private long expiresInSeconds;
    private long resendAfterSeconds;
}
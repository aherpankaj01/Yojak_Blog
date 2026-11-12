package com.yojak.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class UserSummaryResponse {
    private UUID userId;
    private String name;
    private String bio;
    private String avatar;
}
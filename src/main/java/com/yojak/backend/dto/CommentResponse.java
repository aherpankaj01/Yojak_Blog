package com.yojak.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class CommentResponse {

    private UUID id;
    private String content;
    private UUID userId;
    private String userName;
    private LocalDateTime createdAt;
}
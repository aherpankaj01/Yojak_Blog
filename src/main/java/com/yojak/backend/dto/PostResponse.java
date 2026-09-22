package com.yojak.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class PostResponse {

    private String slug;
    private String title;
    private String content;
    private String featuredImage;
    private String status;
    private UUID userId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
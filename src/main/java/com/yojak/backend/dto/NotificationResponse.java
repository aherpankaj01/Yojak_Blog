package com.yojak.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class NotificationResponse {
    private UUID id;
    private String type;
    private UUID actorId;
    private String actorName;
    private String actorAvatar;
    private String postSlug;
    private String postTitle;
    private UUID commentId;
    private boolean read;
    private LocalDateTime createdAt;
}
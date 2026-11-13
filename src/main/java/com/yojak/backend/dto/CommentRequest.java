package com.yojak.backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CommentRequest {

    @NotBlank(message = "Comment cannot be empty")
    private String content;

    private UUID parentId;
}
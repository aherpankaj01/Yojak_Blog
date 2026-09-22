package com.yojak.backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostRequest {

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Slug is required")
    private String slug;

    @NotBlank(message = "Content is required")
    private String content;

    private String featuredImage; // fileId/URL, set after file upload

    @NotBlank(message = "Status is required")
    private String status; // "active" or "inactive"
}
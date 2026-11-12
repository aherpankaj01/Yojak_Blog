package com.yojak.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class SearchResponse {
    private List<UserSummaryResponse> users;
    private List<PostResponse> posts;
}
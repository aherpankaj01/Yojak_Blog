package com.yojak.backend.controller;

import com.yojak.backend.dto.SearchResponse;
import com.yojak.backend.service.PostService;
import com.yojak.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
public class SearchController {

    private final PostService postService;
    private final UserService userService;

    @GetMapping
    public ResponseEntity<SearchResponse> search(@RequestParam("q") String q) {
        String query = q == null ? "" : q.trim();

        if (query.length() < 2) {
            return ResponseEntity.ok(new SearchResponse(List.of(), List.of()));
        }

        return ResponseEntity.ok(new SearchResponse(
                userService.searchUsers(query),
                postService.searchPosts(query)
        ));
    }
}
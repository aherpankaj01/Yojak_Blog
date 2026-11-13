package com.yojak.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class FollowStatusResponse {
    private long followersCount;
    private long followingCount;
    private boolean following;
}
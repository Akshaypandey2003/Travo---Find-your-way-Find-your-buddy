package com.user.DTO;

import java.time.Instant;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CloseFriendResponse {

    private String id;

    private Instant addedAt;

    private UserSummary user;
}
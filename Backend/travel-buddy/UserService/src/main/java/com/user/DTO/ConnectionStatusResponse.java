package com.user.DTO;

import com.user.Enum.FollowStatus;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ConnectionStatusResponse {

    private String userId;
    private FollowStatus status;
}
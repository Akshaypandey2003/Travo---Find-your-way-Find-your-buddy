package com.notification.Clients;

import org.springframework.stereotype.Component;

import com.notification.DTO.UserSummary;

@Component
public class UserServiceFallback implements UserServiceClient {

    @Override
    public UserSummary getUserSummaryById(String userId) {
        return new UserSummary(
            userId,
            "Unknown User",
            null
        );
    }
}
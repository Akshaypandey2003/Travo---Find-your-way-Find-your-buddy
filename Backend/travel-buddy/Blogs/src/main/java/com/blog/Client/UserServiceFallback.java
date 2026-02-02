package com.blog.Client;

import java.util.List;

import org.springframework.stereotype.Component;

@Component
public class UserServiceFallback implements UserServiceClient {

    @Override
    public List<String> getFriendsByUser(String userId) {
        return List.of("Could not fetch friends at this time");
    }
}
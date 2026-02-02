package com.blog.Client;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
    name = "USER-SERVICE",   // Eureka service name (IMPORTANT)
    fallback = UserServiceFallback.class
)
public interface UserServiceClient {

    @GetMapping("/user/internal/get-all-friends/{userId}")
    List<String> getFriendsByUser(@PathVariable String userId);
}

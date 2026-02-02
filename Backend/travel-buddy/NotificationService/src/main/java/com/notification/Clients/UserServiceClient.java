package com.notification.Clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.notification.DTO.UserSummary;

@FeignClient(
    name = "USER-SERVICE",   // Eureka service name (IMPORTANT)
    fallback = UserServiceFallback.class
)
public interface UserServiceClient {

    @GetMapping("/user/internal/summary/{userId}")
    UserSummary getUserSummaryById(@PathVariable String userId);
}

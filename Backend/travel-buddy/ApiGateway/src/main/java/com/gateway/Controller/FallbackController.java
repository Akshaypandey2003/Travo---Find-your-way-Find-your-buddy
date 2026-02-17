package com.gateway.Controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/fallback")
public class FallbackController {

    @RequestMapping("/user-service")
    public Mono<String> userServiceFallback() {
        return Mono.just("User Service is currently unavailable. Please try again later.");
    }

    @RequestMapping("/trip-service")
    public Mono<String> tripServiceFallback() {
        return Mono.just("Trip Service is currently unavailable. Please try again later.");
    }

    @RequestMapping("/chat-service")
    public Mono<String> chatServiceFallback() {
        return Mono.just("Chat Service is currently unavailable.");
    }
    @RequestMapping("/blog-service")
    public Mono<String> blogServiceFallback() {
        return Mono.just("Blog Service is currently unavailable.");
    }
    @RequestMapping("/feedback-service")
    public Mono<String> feedbackServiceFallback() {
        return Mono.just("Feedback Service is currently unavailable.");
    }
    @RequestMapping("/notification-service")
    public Mono<String> notificationServiceFallback() {
        return Mono.just("Notification Service is currently unavailable.");
    }
}
package com.feed.Controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.feed.DTO.FeedItemResponse;
import com.feed.Service.FeedService;

@RestController
@RequestMapping("/api/v1/feed")
public class FeedController {

    private final FeedService feedService;

    public FeedController(FeedService feedService) {
        this.feedService = feedService;
    }

    @GetMapping
    public ResponseEntity<List<FeedItemResponse>> getUserFeed(
            @AuthenticationPrincipal String userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int limit,
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization) {

        List<FeedItemResponse> feed = feedService.getFeedForUser(userId, page, limit, authorization);
        return ResponseEntity.ok(feed);
    }
}

package com.feed.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.events.User.UserCreatedEvent;

@Service
@KafkaListener(
        topics = "${app.kafka.topics.user-events:user-events}",
        groupId = "${spring.kafka.consumer.group-id:user-feed-service}")
public class UserEventConsumer {

    private static final Logger logger = LoggerFactory.getLogger(UserEventConsumer.class);
    private final FeedService feedService;

    public UserEventConsumer(FeedService feedService) {
        this.feedService = feedService;
    }

    @KafkaHandler
    public void handleCreated(UserCreatedEvent event) {
        if (event == null || event.getUserId() == null || event.getUserId().isBlank()) {
            return;
        }
        logger.info("Received user created event userId={}", event.getUserId());
        feedService.initializeUser(event.getUserId());
    }

    @KafkaHandler(isDefault = true)
    public void handleUnknown(Object event) {
        logger.warn("Received unknown user event type={}",
                event == null ? "null" : event.getClass().getName());
    }
}
package com.feed.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.events.Feed.PostCreatedEvent;
import com.events.Feed.PostDeletedEvent;

@Service
@KafkaListener(
        topics = "${app.kafka.topics.post-events:post-events}",
        groupId = "${spring.kafka.consumer.group-id:user-feed-service}")
public class FeedEventConsumer {

    private static final Logger logger = LoggerFactory.getLogger(FeedEventConsumer.class);

    private final FeedService feedService;

    public FeedEventConsumer(FeedService feedService) {
        this.feedService = feedService;
    }

    @KafkaHandler
    public void handlePostCreated(PostCreatedEvent event) {
        logger.info("Received PostCreatedEvent eventId={} resourceId={} authorId={}",
                event.getEventId(), event.getResourceId(), event.getAuthorId());
        feedService.handlePostCreated(event);
    }

    @KafkaHandler
    public void handlePostDeleted(PostDeletedEvent event) {
        logger.info("Received PostDeletedEvent resourceId={} resourceType={}",
                event.getResourceId(), event.getResourceType());
        feedService.handlePostDeleted(event);
    }

    @KafkaHandler(isDefault = true)
    public void handleUnknown(Object event) {
        logger.warn("Received unknown feed event type={}", event == null ? "null" : event.getClass().getName());
    }
}

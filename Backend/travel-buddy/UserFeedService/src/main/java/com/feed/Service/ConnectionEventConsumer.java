package com.feed.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.events.Connection.ConnectionCreatedEvent;
import com.events.Connection.ConnectionRemovedEvent;

@Service
@KafkaListener(
        topics = "${app.kafka.topics.connection-events:connection-events}",
        groupId = "${spring.kafka.consumer.group-id:user-feed-service}")
public class ConnectionEventConsumer {

    private static final Logger logger = LoggerFactory.getLogger(ConnectionEventConsumer.class);
    private final FeedService feedService;
    private final UserConnectionService userConnectionService;

    public ConnectionEventConsumer(FeedService feedService, UserConnectionService userConnectionService) {
        this.feedService = feedService;
        this.userConnectionService = userConnectionService;
    }

    @KafkaHandler
    public void handleCreated(ConnectionCreatedEvent event) {
        if (event == null || event.getFollowerId() == null || event.getFollowingId() == null) {
            return;
        }
        logger.info("Received connection created followerId={} followingId={}",
                event.getFollowerId(), event.getFollowingId());
        userConnectionService.evictFollowersCache(event.getFollowingId());
        feedService.backfillConnection(event.getFollowerId(), event.getFollowingId());
    }

    @KafkaHandler
    public void handleRemoved(ConnectionRemovedEvent event) {
        if (event == null || event.getFollowerId() == null || event.getFollowingId() == null) {
            return;
        }
        logger.info("Received connection removed followerId={} followingId={}",
                event.getFollowerId(), event.getFollowingId());
        userConnectionService.evictFollowersCache(event.getFollowingId());
        feedService.removeConnection(event.getFollowerId(), event.getFollowingId());
    }

    @KafkaHandler(isDefault = true)
    public void handleUnknown(Object event) {
        logger.warn("Received unknown connection event type={}",
                event == null ? "null" : event.getClass().getName());
    }
}
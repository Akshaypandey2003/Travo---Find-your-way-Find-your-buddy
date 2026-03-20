package com.notification.Service;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.events.User.UserUpdatedEvent;
import com.notification.Entity.Notification;
import com.mongodb.client.result.UpdateResult;

@Service
public class UserEventConsumer {

    private static final Logger logger = LoggerFactory.getLogger(UserEventConsumer.class);
    private final MongoTemplate mongoTemplate;

    public UserEventConsumer(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @KafkaListener(
            topics = "${app.kafka.topics.user-events:user-events}",
            groupId = "${spring.kafka.consumer.group-id:notification-service}")
    public void consumeUserUpdatedEvent(UserUpdatedEvent event) {
        if (event == null || event.getUserId() == null) {
            logger.warn("Skipping UserUpdatedEvent because event or userId is null");
            return;
        }

        Map<String, Object> updatedFields = event.getUpdatedFields();
        if (updatedFields == null || updatedFields.isEmpty()) {
            logger.info("UserUpdatedEvent has no updated fields for userId={}", event.getUserId());
            return;
        }

        Update update = new Update();
        boolean hasRelevantChange = false;

        if (updatedFields.containsKey("name")) {
            update.set("senderName", updatedFields.get("name"));
            hasRelevantChange = true;
        }
        if (updatedFields.containsKey("profilePic")) {
            update.set("senderProfilePic", updatedFields.get("profilePic"));
            hasRelevantChange = true;
        }

        if (!hasRelevantChange) {
            logger.info("No notification fields to update for userId={}", event.getUserId());
            return;
        }

        Query query = new Query(Criteria.where("notificationFrom").is(event.getUserId()));
        UpdateResult result = mongoTemplate.updateMulti(query, update, Notification.class);

        logger.info("Updated {} notifications for userId={}", result.getModifiedCount(), event.getUserId());
    }
}

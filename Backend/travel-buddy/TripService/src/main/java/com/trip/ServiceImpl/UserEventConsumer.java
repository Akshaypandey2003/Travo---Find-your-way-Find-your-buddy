package com.trip.ServiceImpl;

import java.time.Instant;
import java.util.Map;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.events.User.UserUpdatedEvent;
import com.mongodb.client.result.UpdateResult;
import com.trip.Entity.Trip;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserEventConsumer {

    private final MongoTemplate mongoTemplate;

    @KafkaListener(
            topics = "${kafka.topic.user-events}",
            groupId = "trip-service",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consumeUserUpdatedEvent(UserUpdatedEvent event) {

        log.info("Received UserUpdatedEvent for userId={}", event.getUserId());

        String userId = event.getUserId();
        Map<String, Object> updatedFields = event.getUpdatedFields();

        if (userId == null || updatedFields == null || updatedFields.isEmpty()) {
            log.info("Skipping UserUpdatedEvent with empty payload userId={}", userId);
            return;
        }

        Update ownerUpdate = new Update();
        Update memberUpdate = new Update();
        boolean hasRelevantChange = false;
        boolean hasOwnerChange = false;
        boolean hasMemberChange = false;

        if (updatedFields.containsKey("name")) {
            ownerUpdate.set("tripOwnerName", updatedFields.get("name"));
            memberUpdate.set("tripMembers.$.name", updatedFields.get("name"));
            hasRelevantChange = true;
            hasOwnerChange = true;
            hasMemberChange = true;
        }

        if (updatedFields.containsKey("profilePic")) {
            ownerUpdate.set("tripOwnerProfilePic", updatedFields.get("profilePic"));
            memberUpdate.set("tripMembers.$.profilePic", updatedFields.get("profilePic"));
            hasRelevantChange = true;
            hasOwnerChange = true;
            hasMemberChange = true;
        }

        if (!hasRelevantChange) {
            log.info("No trip fields to update for userId={}", userId);
            return;
        }

        if (hasOwnerChange) {
            ownerUpdate.set("tripUpdatedAt", Instant.now());
            Query ownerQuery = new Query(Criteria.where("tripOwnerId").is(userId));
            UpdateResult ownerResult = mongoTemplate.updateMulti(ownerQuery, ownerUpdate, Trip.class);
            log.info("Updated {} trip owners for userId={}", ownerResult.getModifiedCount(), userId);
        }
        if (hasMemberChange) {
            Query memberQuery = new Query(Criteria.where("tripMembers.userId").is(userId));
            UpdateResult memberResult = mongoTemplate.updateMulti(memberQuery, memberUpdate, Trip.class);
            log.info("Updated {} trip members for userId={}", memberResult.getModifiedCount(), userId);
        }
    }
}

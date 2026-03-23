package com.blog.ServicesImpl;

import java.time.Instant;
import java.util.Map;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.blog.Entity.Blog;
import com.events.User.UserUpdatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserEventConsumer {

    private final MongoTemplate mongoTemplate;

    @KafkaListener(
            topics = "${kafka.topic.user-events}",
            groupId = "blog-service",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consumeUserUpdatedEvent(UserUpdatedEvent event) {

        log.info("Received UserUpdatedEvent for userId={}", event.getUserId());

        String userId = event.getUserId();
        Map<String, Object> updatedFields = event.getUpdatedFields();

        Query query = new Query(Criteria.where("authorId").is(userId));
        Update update = new Update();
      
        if (updatedFields.containsKey("name")) {
            update.set("authorName", updatedFields.get("name"));
        }

        if (updatedFields.containsKey("profilePic")) {
            update.set("authorProfilePic", updatedFields.get("profilePic"));
        }

        update.set("updatedAt", Instant.now());

        mongoTemplate.updateMulti(query, update, Blog.class);
        log.info("Updated {} blogs for userId={}",  userId);
    }
}

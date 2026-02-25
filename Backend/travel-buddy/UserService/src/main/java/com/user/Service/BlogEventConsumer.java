package com.user.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.events.Blog.BlogCreatedEvent;
import com.events.Blog.BlogDeletedEvent;
import com.user.Repository.UserRepo;

@Component
public class BlogEventConsumer {
    
    private static final Logger logger = LoggerFactory.getLogger(BlogEventConsumer.class);

    private final UserRepo userRepo;

    public BlogEventConsumer(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    @KafkaListener(
            topics = "${app.kafka.topics.blog-events:blog-events}",
            groupId = "${spring.kafka.consumer.group-id:user-service}")
    public void consume(Object event) {
        try {
            if (event instanceof BlogCreatedEvent createdEvent) {
                handleBlogCreated(createdEvent);
                return;
            }

            if (event instanceof BlogDeletedEvent deletedEvent) {
                handleBlogDeleted(deletedEvent);
                return;
            }

            logger.debug("Ignoring unsupported blog event type: {}", event == null ? "null" : event.getClass().getName());
        } catch (Exception ex) {
            logger.error("Failed handling blog event: {}", event, ex);
            throw ex;
        }
    }

    private void handleBlogCreated(BlogCreatedEvent event) {
        if (event.getAuthorId() == null || event.getAuthorId().isBlank()) {
            logger.warn("BlogCreatedEvent missing authorId, blogId={}", event.getId());
            return;
        }
        userRepo.incrementBlogsCount(event.getAuthorId());
        logger.info("Incremented blogsCount for authorId={} blogId={}", event.getAuthorId(), event.getId());
    }

    private void handleBlogDeleted(BlogDeletedEvent event) {
        if (event.getAuthorId() == null || event.getAuthorId().isBlank()) {
            logger.warn("BlogDeletedEvent missing authorId, blogId={}", event.getBlogId());
            return;
        }
        userRepo.decrementBlogsCount(event.getAuthorId());
        logger.info("Decremented blogsCount for authorId={} blogId={}", event.getAuthorId(), event.getBlogId());
    }
}

package com.user.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.events.Feedback.CompanionReviewSubmittedEvent;
import com.user.Entity.ProcessedFeedbackEvent;
import com.user.Entity.User;
import com.user.Repository.ProcessedFeedbackEventRepo;
import com.user.Repository.UserRepo;

@Component
public class FeedbackEventConsumer {

    private static final Logger logger = LoggerFactory.getLogger(FeedbackEventConsumer.class);

    private final UserRepo userRepo;
    private final ProcessedFeedbackEventRepo processedFeedbackEventRepo;

    public FeedbackEventConsumer(
            UserRepo userRepo,
            ProcessedFeedbackEventRepo processedFeedbackEventRepo) {
        this.userRepo = userRepo;
        this.processedFeedbackEventRepo = processedFeedbackEventRepo;
    }

    @KafkaListener(
            topics = "${app.kafka.topics.feedback-events:feedback-events}",
            groupId = "${spring.kafka.consumer.group-id:user-service}")
    public void consume(Object event) {
        try {
            if (event instanceof CompanionReviewSubmittedEvent reviewEvent) {
                handleCompanionReviewSubmitted(reviewEvent);
            }
        } catch (Exception ex) {
            logger.error("Failed to process feedback event={}", event, ex);
            throw ex;
        }
    }

    private void handleCompanionReviewSubmitted(CompanionReviewSubmittedEvent event) {
        if (event.getReviewId() == null || event.getReviewId().isBlank()) {
            logger.warn("Skipping CompanionReviewSubmittedEvent with empty reviewId: {}", event);
            return;
        }
        if (processedFeedbackEventRepo.existsById(event.getReviewId())) {
            logger.info("Duplicate CompanionReviewSubmittedEvent ignored reviewId={}", event.getReviewId());
            return;
        }
        if (event.getTargetUserId() == null || event.getTargetUserId().isBlank()) {
            logger.warn("CompanionReviewSubmittedEvent missing targetUserId reviewId={}", event.getReviewId());
            return;
        }

        User user = userRepo.findById(event.getTargetUserId()).orElse(null);
        if (user == null) {
            logger.warn("Target user not found for reviewId={} targetUserId={}", event.getReviewId(), event.getTargetUserId());
            return;
        }

        int count = Math.max(user.getRatingsReceivedCount(), 0) + 1;
        int sum = Math.max(user.getRatingsReceivedSum(), 0) + event.getRating();
        double average = ((double) sum) / count;

        user.setRatingsReceivedCount(count);
        user.setRatingsReceivedSum(sum);
        user.setAverageRating(average);
        userRepo.save(user);

        processedFeedbackEventRepo.save(ProcessedFeedbackEvent.builder()
                .eventId(event.getReviewId())
                .eventType("COMPANION_REVIEW_SUBMITTED")
                .build());

        logger.info("Updated rating aggregate for userId={} count={} average={}", user.getUserId(), count, average);
    }
}

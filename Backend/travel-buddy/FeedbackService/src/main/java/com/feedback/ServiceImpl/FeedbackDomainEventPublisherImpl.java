package com.feedback.ServiceImpl;

import java.time.Instant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.events.Feedback.AppFeedbackSubmittedEvent;
import com.events.Feedback.CompanionReviewSubmittedEvent;
import com.events.Notification.FailedNotification;
import com.events.Repositories.FailedNotificationRepository;
import com.feedback.Entity.AppFeedback;
import com.feedback.Entity.CompanionReview;
import com.feedback.Service.FeedbackDomainEventPublisher;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;

@Service
public class FeedbackDomainEventPublisherImpl implements FeedbackDomainEventPublisher {

    private static final Logger logger = LoggerFactory.getLogger(FeedbackDomainEventPublisherImpl.class);
    private static final String TOPIC = "feedback-events";

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final FailedNotificationRepository failedOutboundEventRepo;

    public FeedbackDomainEventPublisherImpl(
            KafkaTemplate<String, Object> kafkaTemplate,
            FailedNotificationRepository failedOutboundEventRepo) {
        this.kafkaTemplate = kafkaTemplate;
        this.failedOutboundEventRepo = failedOutboundEventRepo;
    }

    @Override
    @CircuitBreaker(name = "feedbackEventCircuitBreaker", fallbackMethod = "publishCompanionReviewSubmittedFallback")
    @Retry(name = "feedbackEventRetry")
    public void publishCompanionReviewSubmitted(CompanionReview review) {
        CompanionReviewSubmittedEvent event = CompanionReviewSubmittedEvent.builder()
                .reviewId(review.getReviewId())
                .tripId(review.getTripId())
                .reviewerUserId(review.getReviewerUserId())
                .targetUserId(review.getTargetUserId())
                .rating(review.getRating())
                .review(review.getReview())
                .timestamp(System.currentTimeMillis())
                .build();
        publish(review.getReviewId(), event);
    }

    @Override
    @CircuitBreaker(name = "feedbackEventCircuitBreaker", fallbackMethod = "publishAppFeedbackSubmittedFallback")
    @Retry(name = "feedbackEventRetry")
    public void publishAppFeedbackSubmitted(AppFeedback appFeedback) {
        AppFeedbackSubmittedEvent event = AppFeedbackSubmittedEvent.builder()
                .feedbackId(appFeedback.getFeedbackId())
                .userId(appFeedback.getUserId())
                .rating(appFeedback.getRating())
                .comment(appFeedback.getComment())
                .timestamp(System.currentTimeMillis())
                .build();
        publish(appFeedback.getFeedbackId(), event);
    }

    public void publishCompanionReviewSubmittedFallback(CompanionReview review, Exception ex) {
        logger.error("Failed to publish CompanionReviewSubmittedEvent reviewId={}", review.getReviewId(), ex);
        CompanionReviewSubmittedEvent event = CompanionReviewSubmittedEvent.builder()
                .reviewId(review.getReviewId())
                .tripId(review.getTripId())
                .reviewerUserId(review.getReviewerUserId())
                .targetUserId(review.getTargetUserId())
                .rating(review.getRating())
                .review(review.getReview())
                .timestamp(System.currentTimeMillis())
                .build();
        saveFailed(TOPIC, review.getReviewId(), event, ex.getMessage());
    }

    public void publishAppFeedbackSubmittedFallback(AppFeedback appFeedback, Exception ex) {
        logger.error("Failed to publish AppFeedbackSubmittedEvent feedbackId={}", appFeedback.getFeedbackId(), ex);
        AppFeedbackSubmittedEvent event = AppFeedbackSubmittedEvent.builder()
                .feedbackId(appFeedback.getFeedbackId())
                .userId(appFeedback.getUserId())
                .rating(appFeedback.getRating())
                .comment(appFeedback.getComment())
                .timestamp(System.currentTimeMillis())
                .build();
        saveFailed(TOPIC, appFeedback.getFeedbackId(), event, ex.getMessage());
    }

    private void publish(String key, Object payload) {
        try {
            kafkaTemplate.send(TOPIC, key, payload).get();
        } catch (Exception ex) {
            throw new RuntimeException("Failed to publish feedback domain event", ex);
        }
    }

    private void saveFailed(String topic, String key, Object payload, String reason) {
        FailedNotification failed = FailedNotification.builder()
                .topic(topic)
                .key(key)
                .event(payload)
                .retryCount(0)
                .createdAt(Instant.now())
                .lastRetryAt(null)
                .failureReason(reason)
                .build();
        failedOutboundEventRepo.save(failed);
    }
}

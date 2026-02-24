package com.feedback.Service;

import com.feedback.Entity.AppFeedback;
import com.feedback.Entity.CompanionReview;

public interface FeedbackDomainEventPublisher {
    void publishCompanionReviewSubmitted(CompanionReview review);

    void publishAppFeedbackSubmitted(AppFeedback appFeedback);
}

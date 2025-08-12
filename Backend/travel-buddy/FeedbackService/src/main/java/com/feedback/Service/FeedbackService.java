package com.feedback.Service;

import java.util.List;

import com.feedback.Entity.FeedBack;

public interface FeedbackService {
    FeedBack submitFeedback(FeedBack feedback);
    List<FeedBack> getFeedback(String tripId);
    boolean hasUserAlreadySubmitted(String tripId, String userId);
}

package com.feedback.Service;

import com.feedback.DTO.AppFeedbackResponse;
import com.feedback.DTO.CompanionReviewResponse;
import com.feedback.DTO.PageResponseDto;
import com.feedback.DTO.SubmitAppFeedbackRequest;
import com.feedback.DTO.SubmitCompanionReviewRequest;
import com.feedback.DTO.SuggestedCompanionResponse;

public interface FeedbackService {
    CompanionReviewResponse submitCompanionReview(SubmitCompanionReviewRequest request);

    PageResponseDto<CompanionReviewResponse> getTripCompanionReviews(
            String tripId,
            String targetUserId,
            int page,
            int size,
            String sortBy,
            String direction);

    boolean hasUserAlreadySubmittedCompanionReview(String tripId, String reviewerUserId, String targetUserId);

    SuggestedCompanionResponse getSuggestedCompanionsForReview(String tripId, String reviewerUserId, int suggestionLimit);

    AppFeedbackResponse submitAppFeedback(SubmitAppFeedbackRequest request);

    PageResponseDto<AppFeedbackResponse> getAppFeedbackByUser(String userId, int page, int size);
}

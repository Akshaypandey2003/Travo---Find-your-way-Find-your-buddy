package com.feedback.DTO;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SuggestedCompanionResponse {
    private String tripId;
    private String reviewerUserId;
    private int maxReviewsAllowed;
    private long reviewsSubmitted;
    private long reviewsRemaining;
    private List<String> suggestedTargetUserIds;
}

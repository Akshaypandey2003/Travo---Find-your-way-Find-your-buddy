package com.events.Feedback;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CompanionReviewSubmittedEvent {
    private String reviewId;
    private String tripId;
    private String reviewerUserId;
    private String targetUserId;
    private int rating;
    private String review;
    private long timestamp;
}

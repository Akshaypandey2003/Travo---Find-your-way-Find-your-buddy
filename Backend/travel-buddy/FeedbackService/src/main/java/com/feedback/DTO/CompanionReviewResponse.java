package com.feedback.DTO;

import java.time.Instant;
import java.util.Set;

import com.feedback.Entity.CompanionReview;

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
public class CompanionReviewResponse {
    private String reviewId;
    private String tripId;
    private String reviewerUserId;
    private String targetUserId;
    private int rating;
    private String review;
    private Set<String> tags;
    private Instant createdAt;

    public static CompanionReviewResponse from(CompanionReview review) {
        return CompanionReviewResponse.builder()
                .reviewId(review.getReviewId())
                .tripId(review.getTripId())
                .reviewerUserId(review.getReviewerUserId())
                .targetUserId(review.getTargetUserId())
                .rating(review.getRating())
                .review(review.getReview())
                .tags(review.getTags())
                .createdAt(review.getCreatedAt())
                .build();
    }
}

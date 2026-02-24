package com.feedback.DTO;

import java.time.Instant;
import java.util.Set;

import com.feedback.Entity.AppFeedback;

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
public class AppFeedbackResponse {
    private String feedbackId;
    private String userId;
    private String tripId;
    private int rating;
    private String comment;
    private Set<String> featuresLiked;
    private Instant createdAt;

    public static AppFeedbackResponse from(AppFeedback feedback) {
        return AppFeedbackResponse.builder()
                .feedbackId(feedback.getFeedbackId())
                .userId(feedback.getUserId())
                .tripId(feedback.getTripId())
                .rating(feedback.getRating())
                .comment(feedback.getComment())
                .featuresLiked(feedback.getFeaturesLiked())
                .createdAt(feedback.getCreatedAt())
                .build();
    }
}

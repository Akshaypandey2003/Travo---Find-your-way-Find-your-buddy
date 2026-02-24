package com.events.Feedback;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AppFeedbackSubmittedEvent {
    private String feedbackId;
    private String userId;
    private Integer rating;
    private String comment;
    private long timestamp;
}

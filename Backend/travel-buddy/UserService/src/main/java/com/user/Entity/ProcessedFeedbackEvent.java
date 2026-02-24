package com.user.Entity;

import java.time.Instant;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

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
@Document(collection = "processed_feedback_events")
public class ProcessedFeedbackEvent {
    @Id
    private String eventId;
    private String eventType;
    @Builder.Default
    private Instant processedAt = Instant.now();
}

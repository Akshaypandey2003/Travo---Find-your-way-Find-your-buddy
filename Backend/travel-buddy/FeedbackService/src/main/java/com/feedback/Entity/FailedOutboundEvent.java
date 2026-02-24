package com.feedback.Entity;

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
@Document(collection = "feedback_failed_outbound_events")
public class FailedOutboundEvent {
    @Id
    private String id;
    private String topic;
    private String eventKey;
    private Object payload;
    private int retryCount;
    private long createdAt;
    private long lastRetryAt;
    private String failureReason;
}

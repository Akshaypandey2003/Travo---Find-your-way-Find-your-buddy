package com.chat.Events;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatDomainEvent {
    private String eventType;
    private String aggregateType;
    private String aggregateId;
    private String actorUserId;
    private Map<String, Object> payload;
    private long timestamp;
}

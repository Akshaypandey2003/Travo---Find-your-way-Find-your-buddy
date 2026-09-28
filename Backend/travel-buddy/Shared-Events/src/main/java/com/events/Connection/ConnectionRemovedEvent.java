package com.events.Connection;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConnectionRemovedEvent {
    @Builder.Default
    private String eventId = UUID.randomUUID().toString();
    private String followerId;
    private String followingId;
}
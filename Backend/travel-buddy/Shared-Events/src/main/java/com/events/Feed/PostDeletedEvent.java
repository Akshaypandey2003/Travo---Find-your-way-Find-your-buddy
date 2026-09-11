package com.events.Feed;

import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PostDeletedEvent {

    private String eventId;
    private String resourceType;
    private String resourceId;
    private String authorId;

    @Builder.Default
    private Instant deletedAt = Instant.now();
}

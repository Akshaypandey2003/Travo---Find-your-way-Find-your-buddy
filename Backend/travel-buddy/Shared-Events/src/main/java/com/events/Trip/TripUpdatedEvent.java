package com.events.Trip;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TripUpdatedEvent {
    private String tripId;
    private String ownerUserId;
    private Map<String, Object> updatedFields;
    private long timestamp;
}

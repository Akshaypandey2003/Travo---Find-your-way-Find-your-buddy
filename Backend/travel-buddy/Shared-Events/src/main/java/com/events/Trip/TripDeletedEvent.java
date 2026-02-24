package com.events.Trip;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TripDeletedEvent {
    private String tripId;
    private String ownerUserId;
    private long timestamp;
}

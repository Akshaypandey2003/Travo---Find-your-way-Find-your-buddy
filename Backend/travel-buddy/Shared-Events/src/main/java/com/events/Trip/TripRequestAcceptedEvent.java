package com.events.Trip;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TripRequestAcceptedEvent {
    private String tripId;
    private String ownerUserId;
    private String requesterUserId;
    private long timestamp;
}

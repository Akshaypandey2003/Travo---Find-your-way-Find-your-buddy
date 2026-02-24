package com.events.Trip;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TripRequestCancelledEvent {
    private String tripId;
    private String ownerUserId;
    private String requesterUserId;
    private long timestamp;
}

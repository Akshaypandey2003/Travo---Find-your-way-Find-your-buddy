package com.events.Trip;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TripCreatedEvent {
    private String tripId;
    private String ownerUserId;
    private String tripName;
    private LocalDate tripStartDate;
    private LocalDate tripEndDate;
    private int memberCount;
    private int pendingRequestCount;
    private long timestamp;
}

package com.trip.DTO;

import com.trip.Entity.TripRequest.RequestStatus;

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
public class TripRequestActionResponseDto {
    private String tripId;
    private String requesterUserId;
    private RequestStatus status;
    private int pendingRequestCount;
    private String message;
}

package com.trip.DTO;


import java.time.Instant;

import com.trip.Entity.TripRequest;
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
public class TripRequestDto {
    private String requestId;
    private String tripId;
    private String ownerUserId;
    private String requesterUserId;
    private RequestStatus status;
    private Instant requestedAt;
    private Instant actedAt;
    private String actionByUserId;

    public static TripRequestDto from(TripRequest request) {
        return TripRequestDto.builder()
                .requestId(request.getRequestId())
                .tripId(request.getTripId())
                .ownerUserId(request.getOwnerUserId())
                .requesterUserId(request.getRequesterUserId())
                .status(request.getStatus())
                .requestedAt(request.getRequestedAt())
                .actedAt(request.getActedAt())
                .actionByUserId(request.getActionByUserId())
                .build();
    }
}

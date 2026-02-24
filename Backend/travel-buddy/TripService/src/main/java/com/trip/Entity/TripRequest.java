package com.trip.Entity;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

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
@Document(collection = "trip_requests")
@CompoundIndexes({
        @CompoundIndex(name = "trip_request_unique_idx", def = "{'tripId': 1, 'requesterUserId': 1}", unique = true),
        @CompoundIndex(name = "trip_request_status_idx", def = "{'tripId': 1, 'status': 1, 'requestedAt': -1}")
})
public class TripRequest {

    public enum RequestStatus {
        PENDING, ACCEPTED, REJECTED, CANCELLED
    }

    @Id
    private String requestId;

    @NotBlank
    @Indexed
    private String tripId;

    @NotBlank
    @Indexed
    private String ownerUserId;

    @NotBlank
    @Indexed
    private String requesterUserId;

    @NotNull
    private RequestStatus status;

    @Builder.Default
    private LocalDateTime requestedAt = LocalDateTime.now();

    private LocalDateTime actedAt;

    private String actionByUserId;
}

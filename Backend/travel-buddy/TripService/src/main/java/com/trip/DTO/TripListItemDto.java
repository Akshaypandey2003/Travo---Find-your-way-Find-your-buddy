package com.trip.DTO;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.trip.Entity.Trip;
import com.trip.Enum.TripStatus;
import com.trip.Enum.TripType;
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
public class TripListItemDto {

    private String tripId;
    private String tripName;
    private String tripOwnerId;
    private String tripOwnerName;
    private String tripCity;
    private String tripCountry;
    private String tripCategory;
    private LocalDate tripStartDate;
    private LocalDate tripEndDate;
    private TripStatus tripStatus;
    private TripType tripType;
    private int memberCount;
    private int pendingRequestCount;
    private int totalRequestCount;
    private String isPrivateTrip;
    private double tripBudget;
    private LocalDateTime tripCreatedAt;
    private LocalDateTime tripUpdatedAt;

    public static TripListItemDto from(Trip trip) {
        return TripListItemDto.builder()
                .tripId(trip.getTripId())
                .tripName(trip.getTripName())
                .tripOwnerId(trip.getTripOwnerId())
                .tripOwnerName(trip.getTripOwnerName())
                .tripCity(trip.getTripCity())
                .tripCountry(trip.getTripCountry())
                .tripCategory(trip.getTripCategory())
                .tripStartDate(trip.getTripStartDate())
                .tripEndDate(trip.getTripEndDate())
                .tripStatus(trip.getTripStatus())
                .tripType(trip.getTripType())
                .memberCount(trip.getTripMembers() == null ? 0 : trip.getTripMembers().size())
                .pendingRequestCount(trip.getPendingRequestCount())
                .totalRequestCount(trip.getTotalRequestCount())
                .isPrivateTrip(trip.getIsPrivateTrip())
                .tripBudget(trip.getTripBudget())
                .tripCreatedAt(trip.getTripCreatedAt())
                .tripUpdatedAt(trip.getTripUpdatedAt())
                .build();
    }
}

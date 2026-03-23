package com.trip.DTO;

import java.time.Instant;
import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Set;

import com.trip.Entity.Trip;
import com.trip.Entity.TripMember;
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
    private String tripOwnerProfilePic;
    private String tripCity;
    private String tripCountry;
    private String tripCategory;
    private LocalDate tripStartDate;
    private LocalDate tripEndDate;
    private TripStatus tripStatus;
    private TripType tripType;
    private int memberCount;
    @Builder.Default
    private Set<TripMember> tripMembers = new LinkedHashSet<>();
    private int pendingRequestCount;
    private int totalRequestCount;
    private String isPrivateTrip;
    private double tripBudget;
    private Instant tripCreatedAt;
    private Instant tripUpdatedAt;

    public static TripListItemDto from(Trip trip) {
        return TripListItemDto.builder()
                .tripId(trip.getTripId())
                .tripName(trip.getTripName())
                .tripOwnerId(trip.getTripOwnerId())
                .tripOwnerName(trip.getTripOwnerName())
                .tripOwnerProfilePic(trip.getTripOwnerProfilePic())
                .tripCity(trip.getTripCity())
                .tripCountry(trip.getTripCountry())
                .tripCategory(trip.getTripCategory())
                .tripStartDate(trip.getTripStartDate())
                .tripEndDate(trip.getTripEndDate())
                .tripStatus(trip.getTripStatus())
                .tripType(trip.getTripType())
                .memberCount(trip.getTripMembers() == null ? 0 : trip.getTripMembers().size())
                .tripMembers(trip.getTripMembers())
                .pendingRequestCount(trip.getPendingRequestCount())
                .totalRequestCount(trip.getTotalRequestCount())
                .isPrivateTrip(trip.getIsPrivateTrip())
                .tripBudget(trip.getTripBudget())
                .tripCreatedAt(trip.getTripCreatedAt())
                .tripUpdatedAt(trip.getTripUpdatedAt())
                .build();
    }
}

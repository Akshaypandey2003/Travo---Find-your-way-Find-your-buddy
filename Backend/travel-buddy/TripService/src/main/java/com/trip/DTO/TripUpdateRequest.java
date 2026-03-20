package com.trip.DTO;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.trip.Enum.TripStatus;
import com.trip.Enum.TripType;
import com.trip.Entity.TripMember;

import lombok.*;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class TripUpdateRequest {

    private String tripId;
    private String tripName;
    private String tripCity;
    private String tripCountry;
    private String tripState;
    private LocalDate tripStartDate;
    private LocalDate tripEndDate;
    private String tripDuration;
    private String tripDescription;
    private String memberSize;
    private String isPrivateTrip;
    private String tripCategory;
    @Builder.Default

    private Set<String> tripTags = new LinkedHashSet<>();
    private TripType tripType;
    @Builder.Default
    private Set<TripMember> tripMembers = new LinkedHashSet<>();
    
    @Builder.Default
    private TripStatus tripStatus = TripStatus.UPCOMING;

    @Builder.Default
    private int pendingRequestCount = 0;

    @Builder.Default
    private int totalRequestCount = 0;

    private double tripBudget;
    
    @Builder.Default
    private List<String> tripHighlights = new ArrayList<>();

    @Builder.Default
    private List<String> tripImages = new ArrayList<>();


    public boolean isTripDateRangeValid() {
        if (tripStartDate == null || tripEndDate == null) {
            return true;
        }
        return !tripEndDate.isBefore(tripStartDate);
    }
}


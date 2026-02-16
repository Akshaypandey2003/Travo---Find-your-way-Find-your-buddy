package com.trip.Entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.*;


@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder

@Document
public class Trip {
    
    @Id
    private String tripId;
    private String tripName;
    private String tripOwnerId;
    private String tripOwnerName;
    private String tripOwnerProfilePic;
    private String tripCity;
    private String tripCountry;
    private String tripState;
    private LocalDate tripStartDate;
    private LocalDate tripEndDate;
    private String tripDuration;
    private String tripDescription;

    @Builder.Default
    private LocalDateTime tripCreatedAt = LocalDateTime.now();
    private String memberSize;
    
    private String isPrivateTrip;
    private String tripCategory;
    private Set<String> tripTags;
    private TripType tripType;
    private Set<String> tripMembers;
    
    private LocalDateTime tripUpdatedAt;

    private TripStatus tripStatus = TripStatus.UPCOMING;

    private Set<String> tripRequests;
    private double tripBudget;
    
    private List<String> tripHighlights;

    private List<String> tripImages;
    
    public enum TripStatus {
        UPCOMING, ONGOING, COMPLETED, CANCELLED
    }
    public enum TripType {
        SOLO, GROUP, FAMILY
    }

    public Trip() {
        this.tripRequests = new HashSet<>();
        this.tripMembers = new LinkedHashSet<>();
        this.tripHighlights = new ArrayList<>();
        this.tripImages = new ArrayList<>();
    }
}

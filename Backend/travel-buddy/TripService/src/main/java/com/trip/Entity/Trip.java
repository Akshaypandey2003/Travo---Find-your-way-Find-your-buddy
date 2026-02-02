package com.trip.Entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.*;


@AllArgsConstructor
@NoArgsConstructor
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
    //when a new member joins the trip, this field will be updated.
    @Builder.Default
    private Set<String> tripMembers = new LinkedHashSet<>();

    
    
    private LocalDateTime tripUpdatedAt;

    @Builder.Default
    private TripStatus tripStatus = TripStatus.UPCOMING;

    @Builder.Default
    private List<String> tripRequests = new ArrayList<>();
   // To be filled after the completion of trip.
    private double tripBudget;
    
    @Builder.Default
    private List<String> tripHighlights  = new ArrayList<>();

    @Builder.Default
    private List<String> tripImages =new ArrayList<>();
    
    public enum TripStatus {
        UPCOMING, ONGOING, COMPLETED, CANCELLED
    }
    public enum TripType {
        SOLO, GROUP, FAMILY
    }
}

package com.trip.Entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.*;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString

@Document
public class Trip {
    @Id
    private String tripId;
    private String tripName;
    private String createdBy;
    private String tripCity;
    private String tripCountry;
    private String tripState;
    private LocalDate tripDate;
    private String tripDuration;
    private String tripDescription;
    private LocalDateTime tripCreatedAt = LocalDateTime.now();
    private String memberSize;
    
    private String isPrivateTrip;
    private String tripCategory;
    private List<String> tripTags;
    private TripType tripType;
    //when a new member joins the trip, this field will be updated.
    private List<String> tripMembers = new ArrayList<>();

    private LocalDateTime tripUpdatedAt;

    @Builder.Default
    private TripStatus tripStatus = TripStatus.UPCOMING;

    private List<String> tripRequests = new ArrayList<>();
   // To be filled after the completion of trip.
    private double tripBudget;
    private List<String> tripHighlights  = new ArrayList<>();
    private List<String> tripImages =new ArrayList<>();
    public enum TripStatus {
        UPCOMING, ONGOING, COMPLETED, CANCELLED
    }
    public enum TripType {
        SOLO, GROUP, FAMILY
    }
}

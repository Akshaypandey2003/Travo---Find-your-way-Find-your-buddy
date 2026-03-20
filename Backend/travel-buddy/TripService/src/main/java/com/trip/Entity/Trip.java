package com.trip.Entity;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.trip.Enum.TripStatus;
import com.trip.Enum.TripType;

import lombok.*;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Builder
@Document(collection = "trips")
@CompoundIndexes({
        @CompoundIndex(name = "trip_owner_status_idx", def = "{'tripOwnerId': 1, 'tripStatus': 1}"),
        @CompoundIndex(name = "trip_location_date_idx", def = "{'tripCountry': 1, 'tripCity': 1, 'tripStartDate': 1}")
})
public class Trip {
    
    @Id
    private String tripId;

    @NotBlank(message = "Trip name is required")
    @Size(max = 120, message = "Trip name cannot exceed 120 characters")
    private String tripName;

    @NotBlank(message = "Trip owner id is required")
    @Indexed
    private String tripOwnerId;

    @NotBlank(message = "Trip owner name is required")
    @Size(max = 80, message = "Trip owner name cannot exceed 80 characters")
    private String tripOwnerName;

    @Size(max = 512, message = "Trip owner profile pic URL cannot exceed 512 characters")
    private String tripOwnerProfilePic;

    @NotBlank(message = "Trip city is required")
    @Size(max = 80, message = "Trip city cannot exceed 80 characters")
    private String tripCity;

    @NotBlank(message = "Trip country is required")
    @Size(max = 80, message = "Trip country cannot exceed 80 characters")
    private String tripCountry;

    @Size(max = 80, message = "Trip state cannot exceed 80 characters")

    private String tripState;

    @NotNull(message = "Trip start date is required")
    private LocalDate tripStartDate;

    @NotNull(message = "Trip end date is required")
    private LocalDate tripEndDate;

    @Size(max = 40, message = "Trip duration cannot exceed 40 characters")
    private String tripDuration;

    @Size(max = 2000, message = "Trip description cannot exceed 2000 characters")
    private String tripDescription;

    @Builder.Default
    private LocalDateTime tripCreatedAt = LocalDateTime.now();

    @Pattern(regexp = "^\\d{1,3}$", message = "Member size must be a numeric string")
    private String memberSize;
    
    @Pattern(regexp = "^(?i:true|false)$", message = "isPrivateTrip must be true or false")
    private String isPrivateTrip;

    @Indexed
    @Size(max = 60, message = "Trip category cannot exceed 60 characters")
    private String tripCategory;

    @Builder.Default
    private Set<String> tripTags = new LinkedHashSet<>();

    @NotNull(message = "Trip type is required")
    private TripType tripType;

    @Builder.Default
    private Set<TripMember> tripMembers = new LinkedHashSet<>();
    
    private LocalDateTime tripUpdatedAt;

    @Builder.Default
    private TripStatus tripStatus = TripStatus.UPCOMING;

    @Builder.Default
    private int pendingRequestCount = 0;

    @Builder.Default
    private int totalRequestCount = 0;

    @DecimalMin(value = "0.0", inclusive = true, message = "Trip budget cannot be negative")
    private double tripBudget;
    
    @Builder.Default
    private List<String> tripHighlights = new ArrayList<>();

    @Builder.Default
    private List<String> tripImages = new ArrayList<>();
    

    @AssertTrue(message = "Trip end date must be on or after trip start date")
    public boolean isTripDateRangeValid() {
        if (tripStartDate == null || tripEndDate == null) {
            return true;
        }
        return !tripEndDate.isBefore(tripStartDate);
    }
}

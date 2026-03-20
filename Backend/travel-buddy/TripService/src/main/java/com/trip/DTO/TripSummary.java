package com.trip.DTO;

import java.util.Set;

import com.trip.Entity.TripMember;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TripSummary {
    private String tripId;
    private String tripName;
    private Set<TripMember> members;

}

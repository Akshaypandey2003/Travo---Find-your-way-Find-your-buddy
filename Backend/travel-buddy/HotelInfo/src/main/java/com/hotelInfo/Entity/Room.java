package com.hotelInfo.Entity;

import java.util.List;
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

public class Room {
	
    private String roomId;
    private String roomType;
    private String roomCategory;
    private List<String> roomAmenities;
    private String roomDescription;
    private String roomPhotos[];
    private double roomPrice;
    private int roomCapacity;
    private String hotelId;
    private int totalRooms;
    private int availableRooms;
    private String instructions;
    private List<String> additionalServices;
    private String discount;
    private String cancellationPolicy;
}

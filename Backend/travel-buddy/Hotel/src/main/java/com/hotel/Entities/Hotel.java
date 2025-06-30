package com.hotel.Entities;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder

@Document("hotel")
public class Hotel 
{
    @Id
    private String hotelId;
    private String hotelName;
    private String hotelType;
    private String hotelCategory;
    private String hotelAddress;
    private int rating;
    private int totalRatings;
    private String hotelAmenities[];
    private String hotelDescription;
    private String hotelCoverImg;
    private String hotelPhotos[];
    private String hotelContactNumber;
    private String hotelEmail;
    private String hotelWebSite;
    private double latitude;
    private double longitude;
    private String hotelCity;
    private String hotelState;
    private String hotelCountry;
    private String countryCode;
    private String hotelPolicy;
    private String roomTypes[];
    private int totalRooms;    
}

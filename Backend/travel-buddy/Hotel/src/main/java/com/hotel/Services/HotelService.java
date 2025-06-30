package com.hotel.Services;

import java.util.List;

import com.hotel.Entities.Hotel;

public interface HotelService {
    
    public Hotel addHotel(Hotel hotel);
    public List<Hotel> getAllHotels();
    public Hotel getHotelById(String hotelId);
    public List<Hotel> getHotelsByCategory(List<String> category);
    public List<Hotel> getHotelsByType(List<String> type);
    public List<Hotel> getHotelsByCity(String city);
    public List<Hotel> getHotelsByState(String state);
}

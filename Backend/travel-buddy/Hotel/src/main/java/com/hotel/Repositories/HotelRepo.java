package com.hotel.Repositories;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.hotel.Entities.Hotel;

public interface HotelRepo extends MongoRepository<Hotel,String> {
    
    List<Hotel> findByHotelCategoryIn(List<String> categories);
    List<Hotel> findByHotelTypeIn(List<String> types);
    List<Hotel> findByHotelCity(String city);
    List<Hotel> findByHotelState(String state);
}

package com.booking.Repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.booking.Entity.Booking;

public interface BookingRepository extends MongoRepository<Booking,String> {
    
    List<Booking> findByHotelId(String hotelId);
    List<Booking> findByUserId(String userId);
}

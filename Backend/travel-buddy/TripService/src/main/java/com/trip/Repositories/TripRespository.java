package com.trip.Repositories;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.trip.Entity.Trip;

public interface TripRespository extends MongoRepository<Trip,String>{

    Page<Trip> findByTripCategory(String category, Pageable pageable); // Find trips by category

    
    @Query("{ '$or': [ { 'tripOwnerId': ?0 }, { 'tripMembers.userId': ?0 } ] }")
    Page<Trip> findByUserId(String userId, Pageable pageable); // Find trips by user ID

    Page<Trip> findByTripStartDate(LocalDate date, Pageable pageable);
    Page<Trip> findByTripEndDate(LocalDate date, Pageable pageable);

    List<Trip> findByTripOwnerId(String userId);
} 

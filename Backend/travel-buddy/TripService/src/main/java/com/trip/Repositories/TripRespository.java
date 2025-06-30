package com.trip.Repositories;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.trip.Entity.Trip;

public interface TripRespository extends MongoRepository<Trip,String>{

    List<Trip> findByTripCategory(String category); // Find trips by category

    
    @Query("{ '$or': [ { 'createdBy': ?0 }, { 'tripMembers': ?0 } ] }")
    List<Trip> findByCreatedBy(String userId); // Find trips by user ID
} 

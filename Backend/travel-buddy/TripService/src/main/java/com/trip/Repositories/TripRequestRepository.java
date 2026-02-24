package com.trip.Repositories;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.trip.Entity.TripRequest;
import com.trip.Entity.TripRequest.RequestStatus;

public interface TripRequestRepository extends MongoRepository<TripRequest, String> {
    Optional<TripRequest> findByTripIdAndRequesterUserId(String tripId, String requesterUserId);

    long countByTripIdAndStatus(String tripId, RequestStatus status);

    Page<TripRequest> findByTripIdAndStatus(String tripId, RequestStatus status, Pageable pageable);

    void deleteByTripId(String tripId);
}

package com.trip.ServiceImpl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.trip.Entity.Trip;
import com.trip.Exceptions.TripNotFoundException;
import com.trip.Repositories.TripRespository;
import com.trip.Services.TripServices;

@Service
public class TripServiceImpl implements TripServices {

    @Autowired
    private TripRespository tripRespository;

    @Override
    public Trip createTrip(Trip trip) {
        trip.setTripCreatedAt(LocalDateTime.now());
        return tripRespository.save(trip);
    }

    public Map<String,String> deleteTrip(String tripId) {
        Trip existingTrip = tripRespository.findById(tripId)
                .orElseThrow(() -> new TripNotFoundException("Trip not found with id: " + tripId));

        tripRespository.delete(existingTrip);
        Map<String, String> hm = new HashMap<>();
        hm.put("status: ", "trip deleted successfully.");
        return hm;
    }

    @Override
    public Trip updateTrip(String tripId, Trip tripDetails) {

        Trip existingTrip = tripRespository.findById(tripId)
                .orElseThrow(() -> new TripNotFoundException("Trip not found with id: " + tripId));
        if (tripDetails.getTripCity() != null)
            existingTrip.setTripCity(tripDetails.getTripCity());
        if (tripDetails.getTripCountry() != null)
            existingTrip.setTripCountry(tripDetails.getTripCountry());
        if (tripDetails.getTripState() != null)
            existingTrip.setTripState(tripDetails.getTripState());
        if (tripDetails.getTripDate() != null)
            existingTrip.setTripDate(tripDetails.getTripDate());
        if (tripDetails.getTripDuration() != null)
            existingTrip.setTripDuration(tripDetails.getTripDuration());
        if (tripDetails.getTripDescription() != null)
            existingTrip.setTripDescription(tripDetails.getTripDescription());

        existingTrip.setTripUpdatedAt(LocalDateTime.now());

        // if(existingTrip.getMemberSize()!=0)
        // existingTrip.setMemberSize(tripDetails.getMemberSize());
        // existingTrip.setPrivateTrip(tripDetails.isPrivateTrip());
        // existingTrip.setTripCategory(tripDetails.getTripCategory());
        // existingTrip.setTripTags(tripDetails.getTripTags());
        return tripRespository.save(existingTrip);
    }

    @Override
    public Trip getTripById(String tripId) {
        return tripRespository.findById(tripId)
                .orElseThrow(() -> new TripNotFoundException("Trip not found with id: " + tripId));
    }

    @Override
    public List<Trip> getAllTrips() {
        List<Trip> trips = tripRespository.findAll();

        if (trips == null || trips.isEmpty())
            throw new TripNotFoundException("No trips found in the database.");
        return trips;
    }

    @Override
    public List<Trip> getTripsByUserId(String userId) {
        List<Trip> trips = tripRespository.findByCreatedBy(userId);
        if (trips == null || trips.isEmpty())
            throw new TripNotFoundException("No trips found in the database for given user id: " + userId);
        return trips;
    }

    @Override
    public List<Trip> getTripsByCategory(String category) {
        List<Trip> trips = tripRespository.findByTripCategory(category);
        if (trips == null || trips.isEmpty())
            throw new TripNotFoundException("No trips found in the database for given category");
        return trips;
    }

    public Trip sendTripRequest(String tripId, String requestFrom, String requestTo) {

        Trip trip = tripRespository.findById(tripId)
                .orElseThrow(() -> new TripNotFoundException("Trip not found with id: " + tripId));
        List<String> tripRequests = trip.getTripRequests();
        if (tripRequests == null) {
            tripRequests = new ArrayList<>();
        }
        tripRequests.add(requestFrom);
        trip.setTripRequests(tripRequests);
        return tripRespository.save(trip);
    }

    @Override
    public Trip acceptTripRequest(String tripId, String requestFrom) {
       Trip trip = tripRespository.findById(tripId)
                .orElseThrow(() -> new TripNotFoundException("Trip not found with id: " + tripId));

        List<String> tripRequests = trip.getTripRequests();
        if(tripRequests.contains(requestFrom))
        tripRequests.remove(requestFrom);
        trip.setTripRequests(tripRequests);

        List<String> tripMembers = trip.getTripMembers();
        if(tripMembers==null)
        {
            tripMembers = new ArrayList<>();
        }
        if(! tripMembers.contains(requestFrom))
        tripMembers.add(requestFrom);
        trip.setTripMembers(tripMembers);

        return tripRespository.save(trip);
    }

    @Override
    public Trip removeTripMember(String tripId, String memberId) {
        
        Trip trip = tripRespository.findById(tripId)
                .orElseThrow(() -> new TripNotFoundException("Trip not found with id: " + tripId));

       if(trip.getTripMembers()!=null && trip.getTripMembers().size()>0)
       {
        if(trip.getTripMembers().contains(memberId))
        trip.getTripMembers().remove(memberId);
       }
       return tripRespository.save(trip);
    }
}

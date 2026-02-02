package com.trip.ServiceImpl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.trip.Entity.Trip;
import com.trip.Entity.Trip.TripStatus;
import com.trip.Exceptions.TripNotFoundException;
import com.trip.Repositories.TripRespository;
import com.trip.Services.TripNotificationProducer;
import com.trip.Services.TripServices;


@Service
@SuppressWarnings("unused")
public class TripServiceImpl implements TripServices {

    @Autowired
    private TripRespository tripRespository;

    private TripNotificationProducer tripNotificationProducer;

    private static final Logger logger = LoggerFactory.getLogger(TripServiceImpl.class);

    @Override
    public Trip createTrip(Trip trip) {
        trip.setTripCreatedAt(LocalDateTime.now());
        return tripRespository.save(trip);
    }

    public Map<String, String> deleteTrip(String tripId) {
        Trip existingTrip = tripRespository.findById(tripId)
                .orElseThrow(() -> new TripNotFoundException("Trip not found with id: " + tripId));

        tripRespository.delete(existingTrip);
        Map<String, String> hm = new HashMap<>();
        hm.put("status: ", "trip deleted successfully.");
        return hm;
    }

    @Override
    public Trip updateTrip(Trip trip) {
        
        System.out.println("Inside service layer, Trip ID: " + trip.getTripId());
        Trip existingTrip = tripRespository.findById(trip.getTripId())
                .orElseThrow(() -> new TripNotFoundException("Trip not found with id: " + trip.getTripId()));
        if (trip.getTripCity() != null)
            existingTrip.setTripCity(trip.getTripCity());
        if (trip.getTripCountry() != null)
            existingTrip.setTripCountry(trip.getTripCountry());
        if (trip.getTripState() != null)
            existingTrip.setTripState(trip.getTripState());
        if (trip.getTripStartDate() != null)
            existingTrip.setTripStartDate(trip.getTripStartDate());
        if (trip.getTripEndDate() != null)
            existingTrip.setTripEndDate(trip.getTripEndDate());
        if (trip.getTripDuration() != null)
            existingTrip.setTripDuration(trip.getTripDuration());
        if (trip.getTripDescription() != null)
            existingTrip.setTripDescription(trip.getTripDescription());

        existingTrip.setTripUpdatedAt(LocalDateTime.now());
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

        System.out.println("Sending trip request notification from " + requestFrom + " to " + requestTo + " for trip " + tripId);
           
        tripNotificationProducer.sendTripRequestNotification(
                requestFrom,
                requestTo,
                tripId,
                trip.getTripName()
                );

        return tripRespository.save(trip);

    
    }

    @Override
    public Trip acceptTripRequest(String tripId, String requestFrom, String tripOwnerId) {
        Trip trip = tripRespository.findById(tripId)
                .orElseThrow(() -> new TripNotFoundException("Trip not found with id: " + tripId));

        List<String> tripRequests = trip.getTripRequests();
        if (tripRequests.contains(requestFrom))
            tripRequests.remove(requestFrom);
        trip.setTripRequests(tripRequests);

        Set<String> tripMembers = trip.getTripMembers();
        if (tripMembers == null) {
            tripMembers = new LinkedHashSet<>();
        }
        if (!tripMembers.contains(requestFrom))
            tripMembers.add(requestFrom);
        trip.setTripMembers(tripMembers);

         tripNotificationProducer.acceptTripRequestNotification(
                tripOwnerId,
                requestFrom,
                tripId,trip.getTripName());

        return tripRespository.save(trip);
    }

    @Override
    public Trip removeTripMember(String tripId, String memberId) {

        Trip trip = tripRespository.findById(tripId)
                .orElseThrow(() -> new TripNotFoundException("Trip not found with id: " + tripId));

        if (trip.getTripMembers() != null && trip.getTripMembers().size() > 0) {
            if (trip.getTripMembers().contains(memberId))
                trip.getTripMembers().remove(memberId);
        }
        return tripRespository.save(trip);
    }

    @Scheduled(cron = "0 0 10 * * ?", zone = "Asia/Kolkata") // Runs daily at 10:00 AM IST
    public void sendTripReminders() {
        logger.info("Scheduler executed at {}", LocalDateTime.now());
        LocalDate today = LocalDate.now();
        LocalDate tomorrow = today.plusDays(1);
        RestTemplate restTemplate = new RestTemplate();
        String tripServiceUrl = "http://localhost:8088/auth/user/notification/send-notification";

        // Fetch trips starting tomorrow
        List<Trip> upcomingTrips = tripRespository.findByTripStartDate(tomorrow);

        for (Trip trip : upcomingTrips) {
            for (String userId : trip.getTripMembers()) {

                // // Creating notification object
                // Notification notification = new Notification();
                // notification.setNotificationFrom("SYSTEM");
                // notification.setNotificationTo(userId);
                // notification.setTripId(trip.getTripId());
                // notification.setType(Notification.NotificationType.SYSTEM_GENERATED);
                // notification.setMessage("Get ready! Your trip starts tomorrow 🎒");

                // sendNotification(restTemplate, tripServiceUrl, notification);
            }
        }

        // Fetch trips ending today
        List<Trip> endingTrips = tripRespository.findByTripEndDate(today);

        for (Trip trip : endingTrips) {
            if (trip.getTripStatus() != TripStatus.COMPLETED) {
                for (String userId : trip.getTripMembers()) {

                    // Notification notification = new Notification();
                    // notification.setNotificationFrom("SYSTEM");
                    // notification.setNotificationTo(userId);
                    // notification.setTripId(trip.getTripId());
                    // notification.setType(Notification.NotificationType.SYSTEM_GENERATED);
                    // notification
                    //         .setMessage("Hope your trip went well! Please update trip status and share feedback. 📝");

                    // sendNotification(restTemplate, tripServiceUrl, notification);
                }
            }
        }
    }

    // private void sendNotification(RestTemplate restTemplate, String url, Notification notification) {
    //     try {
    //         HttpEntity<Notification> entity = new HttpEntity<>(notification);

    //         ResponseEntity<?> response = restTemplate.exchange(
    //                 url, HttpMethod.POST, entity, new ParameterizedTypeReference<Object>() {
    //                 });
    //         System.out.println("Notification sent: " + response.getStatusCode());
    //     } catch (Exception e) {
    //         System.err.println("Failed to send notification: " + e.getMessage());
    //     }
    // }

}

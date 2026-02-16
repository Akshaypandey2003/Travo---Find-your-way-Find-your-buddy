package com.trip.UnitTests;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.LinkedHashSet;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.trip.Entity.Trip;
import com.trip.Repositories.TripRespository;
import com.trip.ServiceImpl.TripServiceImpl;
import com.trip.Services.TripNotificationProducer;

@ExtendWith(MockitoExtension.class)
class TripServiceTest {

    @Mock
    private TripRespository tripRespository;

    @Mock
    private TripNotificationProducer tripNotificationProducer;

    @InjectMocks
    private TripServiceImpl tripService;

    private Trip trip;

    @BeforeEach
    void setup() {
        trip = new Trip();
        trip.setTripId("t1");
        trip.setTripName("Manali Trip");
        trip.setTripOwnerId("owner1");
        trip.setTripRequests(new LinkedHashSet<>());
        trip.setTripMembers(new LinkedHashSet<>());
    }

    // ---------------- CREATE TRIP ----------------

    @Test
    void createTrip_shouldSetCreatedAt_andSave() {
        when(tripRespository.save(any())).thenAnswer(i -> i.getArgument(0));

        Trip result = tripService.createTrip(trip);

        assertNotNull(result.getTripCreatedAt());
        verify(tripRespository).save(trip);
    }

    // ---------------- GET TRIP ----------------

    @Test
    void getTripById_shouldReturnTrip() {
        when(tripRespository.findById("t1")).thenReturn(Optional.of(trip));

        Trip result = tripService.getTripById("t1");

        assertEquals("t1", result.getTripId());
    }

    // ---------------- UPDATE TRIP ----------------

    @Test
    void updateTrip_shouldUpdateOnlyProvidedFields() {
        when(tripRespository.findById("t1")).thenReturn(Optional.of(trip));
        when(tripRespository.save(any())).thenAnswer(i -> i.getArgument(0));

        Trip update = new Trip();
        update.setTripId("t1");
        update.setTripCity("Delhi");

        Trip result = tripService.updateTrip(update);

        assertEquals("Delhi", result.getTripCity());
        assertNotNull(result.getTripUpdatedAt());
    }

    // ---------------- SEND TRIP REQUEST ----------------

    @Test
    void sendTripRequest_shouldAddRequest_andSendNotification() {
        when(tripRespository.findById("t1")).thenReturn(Optional.of(trip));
        when(tripRespository.save(any())).thenAnswer(i -> i.getArgument(0));

        Trip result = tripService.sendTripRequest("t1", "u1");

        assertTrue(result.getTripRequests().contains("u1"));

        verify(tripNotificationProducer).sendTripRequestNotification(
                "u1",
                "owner1",
                "t1",
                "Manali Trip"
        );
    }

    // ---------------- ACCEPT TRIP REQUEST ----------------

    @Test
    void acceptTripRequest_shouldMoveUserFromRequestsToMembers_andSendNotification() {
        trip.getTripRequests().add("u1");

        when(tripRespository.findById("t1")).thenReturn(Optional.of(trip));
        when(tripRespository.save(any())).thenAnswer(i -> i.getArgument(0));

        Trip result = tripService.acceptTripRequest("t1", "u1");

        assertFalse(result.getTripRequests().contains("u1"));
        assertTrue(result.getTripMembers().contains("u1"));

        verify(tripNotificationProducer).acceptTripRequestNotification(
                "owner1",
                "u1",
                "t1",
                "Manali Trip"
        );
    }

    // ---------------- REMOVE MEMBER ----------------

    @Test
    void removeTripMember_shouldRemoveMember() {
        trip.getTripMembers().add("u1");

        when(tripRespository.findById("t1")).thenReturn(Optional.of(trip));
        when(tripRespository.save(any())).thenAnswer(i -> i.getArgument(0));

        Trip result = tripService.removeTripMember("t1", "u1");

        assertFalse(result.getTripMembers().contains("u1"));
    }

    // ---------------- DELETE TRIP ----------------

    @Test
    void deleteTrip_shouldDeleteTrip() {
        when(tripRespository.findById("t1")).thenReturn(Optional.of(trip));

        tripService.deleteTrip("t1");

        verify(tripRespository).delete(trip);
    }
}

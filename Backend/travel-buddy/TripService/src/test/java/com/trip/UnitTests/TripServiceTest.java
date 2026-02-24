package com.trip.UnitTests;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.trip.DTO.TripDeleteResponseDto;
import com.trip.DTO.TripRequestActionResponseDto;
import com.trip.Entity.Trip;
import com.trip.Entity.Trip.TripType;
import com.trip.Entity.TripRequest;
import com.trip.Entity.TripRequest.RequestStatus;
import com.trip.Exceptions.TripNotFoundException;
import com.trip.Repositories.TripRequestRepository;
import com.trip.Repositories.TripRespository;
import com.trip.ServiceImpl.TripServiceImpl;
import com.trip.Services.TripDomainEventPublisher;
import com.trip.Services.TripNotificationProducer;

@ExtendWith(MockitoExtension.class)
class TripServiceTest {

    @Mock
    private TripRespository tripRespository;
    @Mock
    private TripRequestRepository tripRequestRepository;
    @Mock
    private TripNotificationProducer tripNotificationProducer;
    @Mock
    private TripDomainEventPublisher tripDomainEventPublisher;

    @InjectMocks
    private TripServiceImpl tripService;

    private Trip trip;

    @BeforeEach
    void setUp() {
        trip = Trip.builder()
                .tripId("t1")
                .tripName("Manali")
                .tripOwnerId("owner1")
                .tripOwnerName("Owner")
                .tripCity("Manali")
                .tripCountry("India")
                .tripStartDate(LocalDate.now().plusDays(2))
                .tripEndDate(LocalDate.now().plusDays(5))
                .tripType(TripType.GROUP)
                .tripMembers(new LinkedHashSet<>())
                .build();
    }

    @Test
    void createTrip_success_setsCountersAndSendsNotifications() {
        when(tripRespository.save(any(Trip.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Trip created = tripService.createTrip(trip);

        assertThat(created.getTripCreatedAt()).isNotNull();
        assertThat(created.getPendingRequestCount()).isZero();
        assertThat(created.getTotalRequestCount()).isZero();
        verify(tripNotificationProducer).sendNewTriptNotification("owner1", "t1", "Manali");
        verify(tripDomainEventPublisher).publishTripCreated(created);
    }

    @Test
    void deleteTrip_success_deletesTripAndTripRequestsAndPublishesEvent() {
        when(tripRespository.findById("t1")).thenReturn(Optional.of(trip));

        TripDeleteResponseDto response = tripService.deleteTrip("t1");

        assertThat(response.getStatus()).isEqualTo("trip deleted successfully.");
        verify(tripRespository).delete(trip);
        verify(tripRequestRepository).deleteByTripId("t1");
        verify(tripDomainEventPublisher).publishTripDeleted("t1", "owner1");
    }

    @Test
    void sendTripRequest_success_createsPendingRequest_updatesCounters_andSendsNotification() {
        when(tripRespository.findById("t1")).thenReturn(Optional.of(trip));
        when(tripRequestRepository.findByTripIdAndRequesterUserId("t1", "userA")).thenReturn(Optional.empty());
        when(tripRequestRepository.countByTripIdAndStatus("t1", RequestStatus.PENDING)).thenReturn(1L);
        when(tripRespository.save(any(Trip.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Trip saved = tripService.sendTripRequest("t1", "userA");

        assertThat(saved.getPendingRequestCount()).isEqualTo(1);
        assertThat(saved.getTotalRequestCount()).isEqualTo(1);
        verify(tripRequestRepository).save(any(TripRequest.class));
        verify(tripNotificationProducer).sendTripRequestNotification("userA", "owner1", "t1", "Manali");
    }

    @Test
    void sendTripRequest_whenAlreadyMember_noRequestCreated() {
        trip.getTripMembers().add("userA");
        when(tripRespository.findById("t1")).thenReturn(Optional.of(trip));

        Trip result = tripService.sendTripRequest("t1", "userA");

        assertThat(result.getTripMembers()).contains("userA");
        verify(tripRequestRepository, never()).save(any(TripRequest.class));
        verify(tripNotificationProducer, never()).sendTripRequestNotification(any(), any(), any(), any());
    }

    @Test
    void acceptTripRequest_success_updatesRequestStatus_addsMember_andPublishesDeleteEvent() {
        TripRequest request = TripRequest.builder()
                .requestId("r1")
                .tripId("t1")
                .ownerUserId("owner1")
                .requesterUserId("userA")
                .status(RequestStatus.PENDING)
                .requestedAt(LocalDateTime.now())
                .build();

        when(tripRespository.findById("t1")).thenReturn(Optional.of(trip));
        when(tripRequestRepository.findByTripIdAndRequesterUserId("t1", "userA")).thenReturn(Optional.of(request));
        when(tripRequestRepository.countByTripIdAndStatus("t1", RequestStatus.PENDING)).thenReturn(0L);
        when(tripRespository.save(any(Trip.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Trip saved = tripService.acceptTripRequest("t1", "userA", "notif-1");

        assertThat(saved.getTripMembers()).contains("userA");
        assertThat(saved.getPendingRequestCount()).isZero();
        verify(tripNotificationProducer).acceptTripRequestNotification("owner1", "userA", "t1", "Manali");
        verify(tripDomainEventPublisher).publishNotificationDeleteEvent("owner1", "notif-1");
    }

    @Test
    void acceptTripRequest_failure_whenRequestNotFound() {
        when(tripRespository.findById("t1")).thenReturn(Optional.of(trip));
        when(tripRequestRepository.findByTripIdAndRequesterUserId("t1", "userA")).thenReturn(Optional.empty());

        assertThrows(TripNotFoundException.class, () -> tripService.acceptTripRequest("t1", "userA", "notif-1"));
    }

    @Test
    void rejectTripRequest_success_updatesStatus_andReturnsResponse() {
        TripRequest request = TripRequest.builder()
                .requestId("r1")
                .tripId("t1")
                .ownerUserId("owner1")
                .requesterUserId("userA")
                .status(RequestStatus.PENDING)
                .build();
        when(tripRespository.findById("t1")).thenReturn(Optional.of(trip));
        when(tripRequestRepository.findByTripIdAndRequesterUserId("t1", "userA")).thenReturn(Optional.of(request));
        when(tripRequestRepository.countByTripIdAndStatus("t1", RequestStatus.PENDING)).thenReturn(0L);

        TripRequestActionResponseDto response = tripService.rejectTripRequest("t1", "userA", "owner1");

        assertThat(response.getStatus()).isEqualTo(RequestStatus.REJECTED);
        assertThat(response.getPendingRequestCount()).isZero();
        verify(tripDomainEventPublisher).publishTripRequestRejected("t1", "owner1", "userA");
    }

    @Test
    void cancelTripRequest_success_updatesStatus_andReturnsResponse() {
        TripRequest request = TripRequest.builder()
                .requestId("r1")
                .tripId("t1")
                .ownerUserId("owner1")
                .requesterUserId("userA")
                .status(RequestStatus.PENDING)
                .build();
        when(tripRespository.findById("t1")).thenReturn(Optional.of(trip));
        when(tripRequestRepository.findByTripIdAndRequesterUserId("t1", "userA")).thenReturn(Optional.of(request));
        when(tripRequestRepository.countByTripIdAndStatus("t1", RequestStatus.PENDING)).thenReturn(0L);

        TripRequestActionResponseDto response = tripService.cancelTripRequest("t1", "userA", "userA");

        assertThat(response.getStatus()).isEqualTo(RequestStatus.CANCELLED);
        assertThat(response.getPendingRequestCount()).isZero();
        verify(tripDomainEventPublisher).publishTripRequestCancelled("t1", "owner1", "userA");
    }

    @Test
    void getTripById_failure_whenNotFound() {
        when(tripRespository.findById("t404")).thenReturn(Optional.empty());
        assertThrows(TripNotFoundException.class, () -> tripService.getTripById("t404"));
    }

    @Test
    void removeTripMember_success_removesMemberAndPublishesUpdateEvent() {
        trip.getTripMembers().add("userA");
        when(tripRespository.findById("t1")).thenReturn(Optional.of(trip));
        when(tripRespository.save(any(Trip.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Trip updated = tripService.removeTripMember("t1", "userA");

        assertThat(updated.getTripMembers()).doesNotContain("userA");
        verify(tripDomainEventPublisher).publishTripUpdated(eq(updated), any());
    }
}

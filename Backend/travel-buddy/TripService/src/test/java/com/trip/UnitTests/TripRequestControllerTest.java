package com.trip.UnitTests;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import com.events.Repositories.FailedNotificationRepository;
import com.trip.Config.JwtAuthenticationFilter;
import com.trip.Config.JwtProvider;
import com.trip.Config.SecurityConfig;
import com.trip.Controller.TripRequestController;
import com.trip.DTO.PageResponseDto;
import com.trip.DTO.TripRequestActionResponseDto;
import com.trip.DTO.TripRequestDto;
import com.trip.Entity.Trip;
import com.trip.Entity.Trip.TripType;
import com.trip.Entity.TripRequest.RequestStatus;
import com.trip.Repositories.TripRequestRepository;
import com.trip.Repositories.TripRespository;
import com.trip.Services.TripServices;

@WebMvcTest(controllers = TripRequestController.class)
@Import({ SecurityConfig.class, JwtAuthenticationFilter.class })
@AutoConfigureMockMvc
@SuppressWarnings("removal")
class TripRequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TripServices tripServices;

    @MockBean
    private JwtProvider jwtProvider;

    @MockBean
    private TripRespository tripRespository;

    @MockBean
    private TripRequestRepository tripRequestRepository;

    @MockBean
    private FailedNotificationRepository failedNotificationRepository;

    @BeforeEach
    void setupJwt() {
        when(jwtProvider.validateToken(anyString())).thenReturn(true);
        when(jwtProvider.getUsernameFromToken(anyString()))
                .thenAnswer(inv -> ((String) inv.getArgument(0)).replace("token-", ""));
    }

    private String auth(String userId) {
        return "Bearer token-" + userId;
    }

    private Trip sampleTrip() {
        return Trip.builder()
                .tripId("t1")
                .tripName("Goa")
                .tripOwnerId("owner1")
                .tripOwnerName("Owner")
                .tripCity("Goa")
                .tripCountry("India")
                .tripStartDate(LocalDate.now().plusDays(2))
                .tripEndDate(LocalDate.now().plusDays(4))
                .tripType(TripType.GROUP)
                .tripMembers(new LinkedHashSet<>(List.of("owner1")))
                .build();
    }

    @Test
    void getPendingTripRequests_success_whenOwner() throws Exception {
        Trip trip = sampleTrip();
        PageResponseDto<TripRequestDto> response = PageResponseDto.<TripRequestDto>builder()
                .items(List.of(TripRequestDto.builder()
                        .requestId("r1")
                        .tripId("t1")
                        .ownerUserId("owner1")
                        .requesterUserId("userA")
                        .status(RequestStatus.PENDING)
                        .requestedAt(LocalDateTime.now())
                        .build()))
                .page(0)
                .size(20)
                .totalElements(1)
                .totalPages(1)
                .first(true)
                .last(true)
                .hasNext(false)
                .hasPrevious(false)
                .build();

        when(tripServices.getTripById("t1")).thenReturn(trip);
        when(tripServices.getPendingTripRequests(eq("t1"), eq(0), eq(20), eq("requestedAt"), eq("desc")))
                .thenReturn(response);

        mockMvc.perform(get("/api/v1/trips/t1/requests/pending").header("Authorization", auth("owner1")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items[0].requesterUserId").value("userA"));
    }

    @Test
    void getPendingTripRequests_forbidden_whenNotOwner() throws Exception {
        when(tripServices.getTripById("t1")).thenReturn(sampleTrip());

        mockMvc.perform(get("/api/v1/trips/t1/requests/pending").header("Authorization", auth("not-owner")))
                .andExpect(status().isForbidden());
    }

    @Test
    void getPendingTripRequests_failure_whenInvalidPageSize() throws Exception {
        mockMvc.perform(get("/api/v1/trips/t1/requests/pending").header("Authorization", auth("owner1")).param("size", "101"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void rejectTripRequest_success_whenOwner() throws Exception {
        when(tripServices.getTripById("t1")).thenReturn(sampleTrip());
        when(tripServices.rejectTripRequest("t1", "userA", "owner1"))
                .thenReturn(TripRequestActionResponseDto.builder()
                        .tripId("t1")
                        .requesterUserId("userA")
                        .status(RequestStatus.REJECTED)
                        .pendingRequestCount(0)
                        .message("Trip request rejected successfully")
                        .build());

        mockMvc.perform(patch("/api/v1/trips/t1/requests/userA/reject")
                        .header("Authorization", auth("owner1"))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("REJECTED"));
    }

    @Test
    void rejectTripRequest_forbidden_whenNotOwner() throws Exception {
        when(tripServices.getTripById("t1")).thenReturn(sampleTrip());

        mockMvc.perform(patch("/api/v1/trips/t1/requests/userA/reject")
                        .header("Authorization", auth("intruder"))
                        .with(csrf()))
                .andExpect(status().isForbidden());
    }

    @Test
    void cancelTripRequest_success_whenRequester() throws Exception {
        when(tripServices.getTripById("t1")).thenReturn(sampleTrip());
        when(tripServices.cancelTripRequest("t1", "userA", "userA"))
                .thenReturn(TripRequestActionResponseDto.builder()
                        .tripId("t1")
                        .requesterUserId("userA")
                        .status(RequestStatus.CANCELLED)
                        .pendingRequestCount(0)
                        .message("Trip request cancelled successfully")
                        .build());

        mockMvc.perform(patch("/api/v1/trips/t1/requests/userA/cancel")
                        .header("Authorization", auth("userA"))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CANCELLED"));
    }

    @Test
    void cancelTripRequest_success_whenOwner() throws Exception {
        when(tripServices.getTripById("t1")).thenReturn(sampleTrip());
        when(tripServices.cancelTripRequest("t1", "userA", "owner1"))
                .thenReturn(TripRequestActionResponseDto.builder()
                        .tripId("t1")
                        .requesterUserId("userA")
                        .status(RequestStatus.CANCELLED)
                        .pendingRequestCount(0)
                        .message("Trip request cancelled successfully")
                        .build());

        mockMvc.perform(patch("/api/v1/trips/t1/requests/userA/cancel")
                        .header("Authorization", auth("owner1"))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CANCELLED"));
    }

    @Test
    void cancelTripRequest_forbidden_whenThirdParty() throws Exception {
        when(tripServices.getTripById("t1")).thenReturn(sampleTrip());

        mockMvc.perform(patch("/api/v1/trips/t1/requests/userA/cancel")
                        .header("Authorization", auth("third-party"))
                        .with(csrf()))
                .andExpect(status().isForbidden());
    }
}

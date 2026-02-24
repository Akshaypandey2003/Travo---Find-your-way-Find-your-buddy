package com.trip.UnitTests;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.trip.Config.JwtAuthenticationFilter;
import com.trip.Config.JwtProvider;
import com.trip.Config.SecurityConfig;
import com.trip.Controller.TripController;
import com.trip.DTO.PageResponseDto;
import com.trip.DTO.TripDeleteResponseDto;
import com.trip.DTO.TripListItemDto;
import com.trip.Entity.Trip;
import com.trip.Entity.Trip.TripType;
import com.trip.Services.TripServices;

@WebMvcTest(controllers = TripController.class)
@Import({ SecurityConfig.class, JwtAuthenticationFilter.class })
@AutoConfigureMockMvc
@SuppressWarnings("removal")
class TripControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TripServices tripService;

    @MockBean
    private JwtProvider jwtProvider;

    @Autowired
    private ObjectMapper objectMapper;

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
                .tripName("Manali")
                .tripOwnerId("owner1")
                .tripOwnerName("Owner")
                .tripCity("Manali")
                .tripCountry("India")
                .tripStartDate(LocalDate.now().plusDays(2))
                .tripEndDate(LocalDate.now().plusDays(4))
                .tripType(TripType.GROUP)
                .tripMembers(new LinkedHashSet<>(List.of("owner1")))
                .build();
    }

    @Test
    void createTrip_success_returns201() throws Exception {
        Trip trip = sampleTrip();
        when(tripService.createTrip(org.mockito.ArgumentMatchers.any(Trip.class))).thenReturn(trip);

        mockMvc.perform(post("/api/v1/trips")
                        .header("Authorization", auth("owner1"))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(trip)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.tripId").value("t1"));
    }

    @Test
    void createTrip_failure_whenOwnerMissing() throws Exception {
        Trip request = sampleTrip();
        request.setTripOwnerId(null);

        mockMvc.perform(post("/api/v1/trips")
                        .header("Authorization", auth("owner1"))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createTrip_forbidden_whenOwnerMismatch() throws Exception {
        Trip trip = sampleTrip();
        trip.setTripOwnerId("other-user");

        mockMvc.perform(post("/api/v1/trips")
                        .header("Authorization", auth("owner1"))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(trip)))
                .andExpect(status().isForbidden());
    }

    @Test
    void createTrip_failure_whenValidationFails() throws Exception {
        Trip invalid = sampleTrip();
        invalid.setTripName("");

        mockMvc.perform(post("/api/v1/trips")
                        .header("Authorization", auth("owner1"))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateTrip_forbidden_whenNotOwner() throws Exception {
        Trip existing = sampleTrip();
        when(tripService.getTripById("t1")).thenReturn(existing);

        mockMvc.perform(put("/api/v1/trips")
                        .header("Authorization", auth("not-owner"))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(existing)))
                .andExpect(status().isForbidden());
    }

    @Test
    void updateTrip_success_returns200() throws Exception {
        Trip trip = sampleTrip();
        when(tripService.getTripById("t1")).thenReturn(trip);
        when(tripService.updateTrip(org.mockito.ArgumentMatchers.any(Trip.class))).thenReturn(trip);

        mockMvc.perform(put("/api/v1/trips")
                        .header("Authorization", auth("owner1"))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(trip)))
                .andExpect(status().isOk());
    }

    @Test
    void deleteTrip_success_returns201() throws Exception {
        Trip trip = sampleTrip();
        when(tripService.getTripById("t1")).thenReturn(trip);
        when(tripService.deleteTrip("t1")).thenReturn(TripDeleteResponseDto.builder().status("trip deleted successfully.").build());

        mockMvc.perform(delete("/api/v1/trips/t1")
                        .header("Authorization", auth("owner1"))
                        .with(csrf()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$['status: ']").value("trip deleted successfully."));
    }

    @Test
    void deleteTrip_forbidden_whenNotOwner() throws Exception {
        Trip trip = sampleTrip();
        when(tripService.getTripById("t1")).thenReturn(trip);

        mockMvc.perform(delete("/api/v1/trips/t1")
                        .header("Authorization", auth("userA"))
                        .with(csrf()))
                .andExpect(status().isForbidden());
    }

    @Test
    void sendTripRequest_failure_whenRequestFromMismatch() throws Exception {
        mockMvc.perform(post("/api/v1/trips/send-trip-request/t1/userA")
                        .header("Authorization", auth("another-user"))
                        .with(csrf()))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void sendTripRequest_success_returns200() throws Exception {
        when(tripService.sendTripRequest("t1", "userA")).thenReturn(sampleTrip());

        mockMvc.perform(post("/api/v1/trips/send-trip-request/t1/userA")
                        .header("Authorization", auth("userA"))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"));
    }

    @Test
    void acceptTripRequest_success_returns200() throws Exception {
        Trip trip = sampleTrip();
        when(tripService.getTripById("t1")).thenReturn(trip);
        when(tripService.acceptTripRequest("t1", "userA", "n1")).thenReturn(trip);

        mockMvc.perform(post("/api/v1/trips/accept-trip-request/n1/t1/userA")
                        .header("Authorization", auth("owner1"))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"));
    }

    @Test
    void acceptTripRequest_failure_whenNotOwner_returns500() throws Exception {
        Trip trip = sampleTrip();
        when(tripService.getTripById("t1")).thenReturn(trip);

        mockMvc.perform(post("/api/v1/trips/accept-trip-request/n1/t1/userA")
                        .header("Authorization", auth("another-user"))
                        .with(csrf()))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void getAllTrips_success_returnsPage() throws Exception {
        PageResponseDto<TripListItemDto> page = PageResponseDto.<TripListItemDto>builder()
                .items(List.of(TripListItemDto.from(sampleTrip())))
                .page(0).size(20).totalElements(1).totalPages(1)
                .first(true).last(true).hasNext(false).hasPrevious(false)
                .build();
        when(tripService.getAllTrips(0, 20, "tripCreatedAt", "desc")).thenReturn(page);

        mockMvc.perform(get("/api/v1/trips/get-all-trips").header("Authorization", auth("userA")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    void getAllTrips_failure_whenInvalidPageSize() throws Exception {
        mockMvc.perform(get("/api/v1/trips/get-all-trips").header("Authorization", auth("userA")).param("size", "101"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getTripsByUser_forbidden_whenPrincipalDiffers() throws Exception {
        mockMvc.perform(get("/api/v1/trips/get-trips-by-user/userX").header("Authorization", auth("userY")))
                .andExpect(status().isForbidden());
    }

    @Test
    void getTripsByCategory_success_returns200() throws Exception {
        PageResponseDto<TripListItemDto> page = PageResponseDto.<TripListItemDto>builder()
                .items(List.of(TripListItemDto.from(sampleTrip())))
                .page(0).size(20).totalElements(1).totalPages(1)
                .first(true).last(true).hasNext(false).hasPrevious(false)
                .build();
        when(tripService.getTripsByCategory(eq("Adventure"), eq(0), eq(20), eq("tripCreatedAt"), eq("desc")))
                .thenReturn(page);

        mockMvc.perform(get("/api/v1/trips/get-trips-by-category/Adventure").header("Authorization", auth("userA")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items[0].tripId").value("t1"));
    }

    @Test
    void removeTripMember_forbidden_whenUnauthorizedUser() throws Exception {
        Trip trip = sampleTrip();
        when(tripService.getTripById("t1")).thenReturn(trip);

        mockMvc.perform(delete("/api/v1/trips/remove-trip-member/t1/memberA")
                        .header("Authorization", auth("intruder"))
                        .with(csrf()))
                .andExpect(status().isForbidden());
    }

    @Test
    void removeTripMember_success_whenMemberSelfRemoves() throws Exception {
        Trip trip = sampleTrip();
        trip.getTripMembers().add("memberA");
        when(tripService.getTripById("t1")).thenReturn(trip);
        when(tripService.removeTripMember("t1", "memberA")).thenReturn(trip);

        mockMvc.perform(delete("/api/v1/trips/remove-trip-member/t1/memberA")
                        .header("Authorization", auth("memberA"))
                        .with(csrf()))
                .andExpect(status().isOk());
    }
}

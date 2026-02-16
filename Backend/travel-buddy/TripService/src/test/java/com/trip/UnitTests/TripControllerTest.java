package com.trip.UnitTests;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.trip.Config.JwtProvider;
import com.trip.Controller.TripController;
import com.trip.Entity.Trip;
import com.trip.Services.TripServices;

@WebMvcTest(TripController.class)
@AutoConfigureMockMvc(addFilters = false)
@SuppressWarnings({"removal"})
public class TripControllerTest {
     @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TripServices tripService;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private JwtProvider jwtProvider; 

    private Trip getSampleTrip() {
        Trip trip = new Trip();
        trip.setTripId("t1");
        trip.setTripName("Manali Trip");
        return trip;
    }

    @Test
    void createTrip_shouldReturn201() throws Exception {
        Trip trip = getSampleTrip();

        when(tripService.createTrip(any())).thenReturn(trip);

        mockMvc.perform(post("/trip/create-trip")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(trip)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.tripId").value("t1"));
    }

    @Test
    void updateTrip_shouldReturn200() throws Exception {
        Trip trip = getSampleTrip();

        when(tripService.updateTrip(any())).thenReturn(trip);

        mockMvc.perform(put("/trip/update-trip")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(trip)))
                .andExpect(status().isOk());
    }

    @Test
    void deleteTrip_shouldReturn201() throws Exception {
        Map<String, String> response = Map.of("status", "trip deleted successfully.");

        when(tripService.deleteTrip("t1")).thenReturn(response);

        mockMvc.perform(delete("/trip/delete-trip/t1"))
                .andExpect(status().isCreated());
    }

    @Test
    void sendTripRequest_success() throws Exception {
        when(tripService.sendTripRequest("t1", "u1"))
                .thenReturn(getSampleTrip());

        mockMvc.perform(post("/trip/send-trip-request/t1/u1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"));
    }

    @Test
    void sendTripRequest_failure() throws Exception {
        when(tripService.sendTripRequest(any(), any()))
                .thenThrow(new RuntimeException("error"));

        mockMvc.perform(post("/trip/send-trip-request/t1/u1"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void acceptTripRequest_success() throws Exception {
        when(tripService.acceptTripRequest("t1", "u1"))
                .thenReturn(getSampleTrip());

        mockMvc.perform(post("/trip/accept-trip-request/n1/t1/u1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"));
    }

    @Test
    void acceptTripRequest_failure() throws Exception {
        when(tripService.acceptTripRequest(any(), any()))
                .thenThrow(new RuntimeException("error"));

        mockMvc.perform(post("/trip/accept-trip-request/n1/t1/u1"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void getTripById_shouldReturnTrip() throws Exception {
        when(tripService.getTripById("t1")).thenReturn(getSampleTrip());

        mockMvc.perform(get("/trip/get-trip/t1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tripId").value("t1"));
    }

    @Test
    void getAllTrips_shouldReturnList() throws Exception {
        when(tripService.getAllTrips())
                .thenReturn(List.of(getSampleTrip()));

        mockMvc.perform(get("/trip/get-all-trips"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].tripId").value("t1"));
    }

    @Test
    void getTripsByUserId_shouldReturnList() throws Exception {
        when(tripService.getTripsByUserId("u1"))
                .thenReturn(List.of(getSampleTrip()));

        mockMvc.perform(get("/trip/get-trips-by-user/u1"))
                .andExpect(status().isOk());
    }

    @Test
    void getTripsByCategory_shouldReturnList() throws Exception {
        when(tripService.getTripsByCategory("Adventure"))
                .thenReturn(List.of(getSampleTrip()));

        mockMvc.perform(get("/trip/get-trips-by-category/Adventure"))
                .andExpect(status().isOk());
    }

    @Test
    void removeTripMember_shouldReturnTrip() throws Exception {
        when(tripService.removeTripMember("t1", "u1"))
                .thenReturn(getSampleTrip());

        mockMvc.perform(delete("/trip/remove-trip-member/t1/u1"))
                .andExpect(status().isOk());
    }
}

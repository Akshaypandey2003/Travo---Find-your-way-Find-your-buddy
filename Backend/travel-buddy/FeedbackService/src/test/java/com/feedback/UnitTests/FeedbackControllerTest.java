package com.feedback.UnitTests;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.feedback.Config.JwtProvider;
import com.feedback.Controller.FeedbackController;
import com.feedback.Entity.FeedBack;
import com.feedback.Service.FeedbackService;

@WebMvcTest(FeedbackController.class)
@AutoConfigureMockMvc(addFilters = false)
@SuppressWarnings({"removal","unused"})
class FeedbackControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FeedbackService feedbackService;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private JwtProvider jwtProvider;

    // ---------------- POST /feedback/submit ----------------

    @Test
    void submitFeedback_shouldReturnCreated() throws Exception {
        FeedBack feedback = new FeedBack();
        feedback.setTripId("trip123");
        feedback.setAuthorId("user1");
        feedback.setComment("Great trip!");

        when(feedbackService.submitFeedback(any(FeedBack.class)))
                .thenReturn(feedback);

        mockMvc.perform(post("/feedback/submit")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(feedback)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.tripId").value("trip123"))
                .andExpect(jsonPath("$.authorId").value("user1"))
                .andExpect(jsonPath("$.comment").value("Great trip!"));

        verify(feedbackService, times(1)).submitFeedback(any(FeedBack.class));
    }

    // ---------------- GET /feedback/get-trip-feedback/{tripId} ----------------

    @Test
    void getFeedBack_shouldReturnFeedbackList() throws Exception {
        FeedBack feedback = new FeedBack();
        feedback.setTripId("trip123");
        feedback.setAuthorId("user1");
        feedback.setComment("Nice trip");

        List<FeedBack> feedbackList = List.of(feedback);

        when(feedbackService.getFeedback("trip123"))
                .thenReturn(feedbackList);

        mockMvc.perform(get("/feedback/get-trip-feedback/{tripId}", "trip123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].tripId").value("trip123"))
                .andExpect(jsonPath("$[0].authorId").value("user1"));

        verify(feedbackService, times(1)).getFeedback("trip123");
    }

    // ---------------- GET /feedback/check/{tripId}/{userId} ----------------

    @Test
    void checkIfSubmitted_shouldReturnTrue() throws Exception {
        when(feedbackService.hasUserAlreadySubmitted("trip123", "user1"))
                .thenReturn(true);

        mockMvc.perform(get("/feedback/check/{tripId}/{userId}", "trip123", "user1"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));

        verify(feedbackService).hasUserAlreadySubmitted("trip123", "user1");
    }

    @Test
    void checkIfSubmitted_shouldReturnFalse() throws Exception {
        when(feedbackService.hasUserAlreadySubmitted("trip123", "user2"))
                .thenReturn(false);

        mockMvc.perform(get("/feedback/check/{tripId}/{userId}", "trip123", "user2"))
                .andExpect(status().isOk())
                .andExpect(content().string("false"));

        verify(feedbackService).hasUserAlreadySubmitted("trip123", "user2");
    }
}

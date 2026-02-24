package com.feedback.UnitTests;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Instant;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.feedback.Config.JwtAuthenticationFilter;
import com.feedback.Config.JwtProvider;
import com.feedback.Controller.FeedbackController;
import com.feedback.DTO.AppFeedbackResponse;
import com.feedback.DTO.CompanionReviewResponse;
import com.feedback.DTO.PageResponseDto;
import com.feedback.DTO.SubmitAppFeedbackRequest;
import com.feedback.DTO.SubmitCompanionReviewRequest;
import com.feedback.DTO.SuggestedCompanionResponse;
import com.feedback.Exceptions.FeedbackAlreadySubmittedException;
import com.feedback.Service.FeedbackService;

@WebMvcTest(FeedbackController.class)
@AutoConfigureMockMvc(addFilters = false)
@SuppressWarnings("removal")
class FeedbackControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private FeedbackService feedbackService;

    @MockBean
    private JwtProvider jwtProvider;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Test
    void submitCompanionReview_shouldReturnCreated() throws Exception {
        SubmitCompanionReviewRequest request = SubmitCompanionReviewRequest.builder()
                .tripId("trip-1")
                .reviewerUserId("user-1")
                .targetUserId("user-2")
                .rating(5)
                .review("Great travel buddy")
                .tags(Set.of("friendly"))
                .build();

        CompanionReviewResponse response = CompanionReviewResponse.builder()
                .reviewId("review-1")
                .tripId("trip-1")
                .reviewerUserId("user-1")
                .targetUserId("user-2")
                .rating(5)
                .review("Great travel buddy")
                .tags(Set.of("friendly"))
                .createdAt(Instant.now())
                .build();

        when(feedbackService.submitCompanionReview(any(SubmitCompanionReviewRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/feedback/companion-reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.reviewId").value("review-1"))
                .andExpect(jsonPath("$.tripId").value("trip-1"))
                .andExpect(jsonPath("$.reviewerUserId").value("user-1"))
                .andExpect(jsonPath("$.targetUserId").value("user-2"));

        verify(feedbackService).submitCompanionReview(any(SubmitCompanionReviewRequest.class));
    }

    @Test
    void submitCompanionReview_shouldReturnBadRequest_whenValidationFails() throws Exception {
        SubmitCompanionReviewRequest invalid = SubmitCompanionReviewRequest.builder()
                .tripId("trip-1")
                .reviewerUserId("user-1")
                .targetUserId("user-2")
                .rating(0)
                .review("invalid rating")
                .build();

        mockMvc.perform(post("/api/v1/feedback/companion-reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void submitCompanionReview_shouldReturnBadRequest_whenServiceThrows() throws Exception {
        SubmitCompanionReviewRequest request = SubmitCompanionReviewRequest.builder()
                .tripId("trip-1")
                .reviewerUserId("user-1")
                .targetUserId("user-2")
                .rating(5)
                .review("duplicate")
                .build();

        when(feedbackService.submitCompanionReview(any(SubmitCompanionReviewRequest.class)))
                .thenThrow(new FeedbackAlreadySubmittedException("Companion review already submitted for this user in this trip."));

        mockMvc.perform(post("/api/v1/feedback/companion-reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Companion review already submitted for this user in this trip."));
    }

    @Test
    void getTripCompanionReviews_shouldReturnPage() throws Exception {
        CompanionReviewResponse item = CompanionReviewResponse.builder()
                .reviewId("review-1")
                .tripId("trip-1")
                .reviewerUserId("user-1")
                .targetUserId("user-2")
                .rating(4)
                .review("Nice")
                .createdAt(Instant.now())
                .build();

        PageResponseDto<CompanionReviewResponse> page = PageResponseDto.<CompanionReviewResponse>builder()
                .items(List.of(item))
                .page(0)
                .size(20)
                .totalElements(1)
                .totalPages(1)
                .first(true)
                .last(true)
                .hasNext(false)
                .hasPrevious(false)
                .build();

        when(feedbackService.getTripCompanionReviews("trip-1", null, 0, 20, "createdAt", "desc"))
                .thenReturn(page);

        mockMvc.perform(get("/api/v1/feedback/trips/{tripId}/companion-reviews", "trip-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items.length()").value(1))
                .andExpect(jsonPath("$.items[0].reviewId").value("review-1"))
                .andExpect(jsonPath("$.totalElements").value(1));

        verify(feedbackService).getTripCompanionReviews("trip-1", null, 0, 20, "createdAt", "desc");
    }

    @Test
    void hasSubmittedCompanionReview_shouldReturnBoolean() throws Exception {
        when(feedbackService.hasUserAlreadySubmittedCompanionReview("trip-1", "user-1", "user-2")).thenReturn(true);

        mockMvc.perform(get("/api/v1/feedback/companion-reviews/check/{tripId}/{reviewerUserId}/{targetUserId}", "trip-1", "user-1", "user-2"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));

        verify(feedbackService).hasUserAlreadySubmittedCompanionReview("trip-1", "user-1", "user-2");
    }

    @Test
    void getSuggestedCompanionsForReview_shouldReturnSuggestions() throws Exception {
        SuggestedCompanionResponse response = SuggestedCompanionResponse.builder()
                .tripId("trip-1")
                .reviewerUserId("user-1")
                .maxReviewsAllowed(3)
                .reviewsSubmitted(1)
                .reviewsRemaining(2)
                .suggestedTargetUserIds(List.of("user-3", "user-4"))
                .build();

        when(feedbackService.getSuggestedCompanionsForReview("trip-1", "user-1", 5)).thenReturn(response);

        mockMvc.perform(get("/api/v1/feedback/trips/{tripId}/suggested-companions/{reviewerUserId}", "trip-1", "user-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.suggestedTargetUserIds.length()").value(2))
                .andExpect(jsonPath("$.suggestedTargetUserIds[0]").value("user-3"));

        verify(feedbackService).getSuggestedCompanionsForReview("trip-1", "user-1", 5);
    }

    @Test
    void submitAppFeedback_shouldReturnCreated() throws Exception {
        SubmitAppFeedbackRequest request = SubmitAppFeedbackRequest.builder()
                .userId("user-1")
                .tripId("trip-1")
                .rating(5)
                .comment("Loved it")
                .featuresLiked(Set.of("chat", "map"))
                .build();

        AppFeedbackResponse response = AppFeedbackResponse.builder()
                .feedbackId("app-fb-1")
                .userId("user-1")
                .tripId("trip-1")
                .rating(5)
                .comment("Loved it")
                .featuresLiked(Set.of("chat", "map"))
                .createdAt(Instant.now())
                .build();

        when(feedbackService.submitAppFeedback(any(SubmitAppFeedbackRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/feedback/app-feedback")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.feedbackId").value("app-fb-1"))
                .andExpect(jsonPath("$.userId").value("user-1"));

        verify(feedbackService).submitAppFeedback(any(SubmitAppFeedbackRequest.class));
    }

    @Test
    void getAppFeedbackByUser_shouldReturnPage() throws Exception {
        AppFeedbackResponse item = AppFeedbackResponse.builder()
                .feedbackId("fb-1")
                .userId("user-1")
                .tripId("trip-1")
                .rating(4)
                .comment("Good")
                .createdAt(Instant.now())
                .build();

        PageResponseDto<AppFeedbackResponse> page = PageResponseDto.<AppFeedbackResponse>builder()
                .items(List.of(item))
                .page(0)
                .size(20)
                .totalElements(1)
                .totalPages(1)
                .first(true)
                .last(true)
                .hasNext(false)
                .hasPrevious(false)
                .build();

        when(feedbackService.getAppFeedbackByUser("user-1", 0, 20)).thenReturn(page);

        mockMvc.perform(get("/api/v1/feedback/app-feedback/users/{userId}", "user-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items.length()").value(1))
                .andExpect(jsonPath("$.items[0].feedbackId").value("fb-1"));

        verify(feedbackService).getAppFeedbackByUser(eq("user-1"), eq(0), eq(20));
    }
}

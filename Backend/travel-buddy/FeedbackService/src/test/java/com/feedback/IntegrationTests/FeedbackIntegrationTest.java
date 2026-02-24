package com.feedback.IntegrationTests;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.feedback.Clients.TripClient;
import com.feedback.DTO.SubmitAppFeedbackRequest;
import com.feedback.DTO.SubmitCompanionReviewRequest;
import com.feedback.DTO.TripSummary;
import com.feedback.Entity.AppFeedback;
import com.feedback.Entity.CompanionReview;
import com.feedback.Repository.AppFeedbackRepo;
import com.feedback.Repository.CompanionReviewRepo;
import com.feedback.Service.FeedbackDomainEventPublisher;
import com.feedback.Service.NotificationProducer;

@SpringBootTest
@Testcontainers(disabledWithoutDocker = true)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@SuppressWarnings("removal")
class FeedbackIntegrationTest {

    @Container
    static MongoDBContainer mongo = new MongoDBContainer("mongo:7.0");

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongo::getReplicaSetUrl);
        registry.add("spring.kafka.bootstrap-servers", () -> "localhost:9092");
        registry.add("spring.task.scheduling.enabled", () -> "false");
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CompanionReviewRepo companionReviewRepo;

    @Autowired
    private AppFeedbackRepo appFeedbackRepo;

    @MockBean
    private TripClient tripClient;

    @MockBean
    private NotificationProducer notificationProducer;

    @MockBean
    private FeedbackDomainEventPublisher feedbackDomainEventPublisher;

    @AfterEach
    void cleanup() {
        companionReviewRepo.deleteAll();
        appFeedbackRepo.deleteAll();
    }

    @Test
    void submitCompanionReview_shouldPersistAndReturnCreated() throws Exception {
        SubmitCompanionReviewRequest request = SubmitCompanionReviewRequest.builder()
                .tripId("trip-1")
                .reviewerUserId("user-1")
                .targetUserId("user-2")
                .rating(5)
                .review("Excellent teammate")
                .tags(new LinkedHashSet<>(Set.of("friendly", "helpful")))
                .build();

        when(tripClient.getTripSummaryById("trip-1")).thenReturn(TripSummary.builder()
                .tripId("trip-1")
                .tripName("Goa Trip")
                .members(Set.of("user-1", "user-2", "user-3"))
                .build());

        mockMvc.perform(post("/api/v1/feedback/companion-reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.tripId").value("trip-1"))
                .andExpect(jsonPath("$.reviewerUserId").value("user-1"))
                .andExpect(jsonPath("$.targetUserId").value("user-2"));

        List<CompanionReview> reviews = companionReviewRepo.findAll();
        assertThat(reviews).hasSize(1);
        assertThat(reviews.get(0).getTripId()).isEqualTo("trip-1");
        assertThat(reviews.get(0).getReviewerUserId()).isEqualTo("user-1");
        assertThat(reviews.get(0).getTargetUserId()).isEqualTo("user-2");
    }

    @Test
    void submitCompanionReview_shouldRejectDuplicate() throws Exception {
        companionReviewRepo.save(CompanionReview.builder()
                .tripId("trip-1")
                .reviewerUserId("user-1")
                .targetUserId("user-2")
                .rating(4)
                .review("Already reviewed")
                .build());

        SubmitCompanionReviewRequest duplicate = SubmitCompanionReviewRequest.builder()
                .tripId("trip-1")
                .reviewerUserId("user-1")
                .targetUserId("user-2")
                .rating(5)
                .review("Duplicate")
                .build();

        when(tripClient.getTripSummaryById("trip-1")).thenReturn(TripSummary.builder()
                .tripId("trip-1")
                .tripName("Goa Trip")
                .members(Set.of("user-1", "user-2"))
                .build());

        mockMvc.perform(post("/api/v1/feedback/companion-reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(duplicate)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Companion review already submitted for this user in this trip."));

        assertThat(companionReviewRepo.findAll()).hasSize(1);
    }

    @Test
    void getTripCompanionReviews_shouldReturnPersistedData() throws Exception {
        companionReviewRepo.save(CompanionReview.builder()
                .tripId("trip-1")
                .reviewerUserId("user-1")
                .targetUserId("user-2")
                .rating(5)
                .review("Great")
                .build());

        mockMvc.perform(get("/api/v1/feedback/trips/{tripId}/companion-reviews", "trip-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items.length()").value(1))
                .andExpect(jsonPath("$.items[0].tripId").value("trip-1"))
                .andExpect(jsonPath("$.items[0].reviewerUserId").value("user-1"));
    }

    @Test
    void hasSubmittedCompanionReview_shouldReturnTrueWhenExists() throws Exception {
        companionReviewRepo.save(CompanionReview.builder()
                .tripId("trip-1")
                .reviewerUserId("user-1")
                .targetUserId("user-2")
                .rating(4)
                .review("Nice")
                .build());

        mockMvc.perform(get("/api/v1/feedback/companion-reviews/check/{tripId}/{reviewerUserId}/{targetUserId}",
                        "trip-1", "user-1", "user-2"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    void submitAppFeedback_shouldPersistAndReturnCreated() throws Exception {
        SubmitAppFeedbackRequest request = SubmitAppFeedbackRequest.builder()
                .userId("user-1")
                .tripId("trip-1")
                .rating(5)
                .comment("Excellent app")
                .featuresLiked(Set.of("chat", "notifications"))
                .build();

        mockMvc.perform(post("/api/v1/feedback/app-feedback")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userId").value("user-1"))
                .andExpect(jsonPath("$.rating").value(5));

        List<AppFeedback> feedbacks = appFeedbackRepo.findAll();
        assertThat(feedbacks).hasSize(1);
        assertThat(feedbacks.get(0).getUserId()).isEqualTo("user-1");
        assertThat(feedbacks.get(0).getTripId()).isEqualTo("trip-1");
    }

    @Test
    void getAppFeedbackByUser_shouldReturnUserFeedbackPage() throws Exception {
        appFeedbackRepo.save(AppFeedback.builder()
                .userId("user-1")
                .tripId("trip-1")
                .rating(5)
                .comment("Great")
                .build());

        appFeedbackRepo.save(AppFeedback.builder()
                .userId("user-2")
                .tripId("trip-2")
                .rating(3)
                .comment("Average")
                .build());

        mockMvc.perform(get("/api/v1/feedback/app-feedback/users/{userId}", "user-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items.length()").value(1))
                .andExpect(jsonPath("$.items[0].userId").value("user-1"));
    }
}

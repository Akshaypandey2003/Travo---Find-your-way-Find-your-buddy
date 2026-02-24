package com.feedback.UnitTests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.util.ReflectionTestUtils;

import com.feedback.Clients.TripClient;
import com.feedback.DTO.AppFeedbackResponse;
import com.feedback.DTO.CompanionReviewResponse;
import com.feedback.DTO.PageResponseDto;
import com.feedback.DTO.SubmitAppFeedbackRequest;
import com.feedback.DTO.SubmitCompanionReviewRequest;
import com.feedback.DTO.SuggestedCompanionResponse;
import com.feedback.DTO.TripSummary;
import com.feedback.Entity.AppFeedback;
import com.feedback.Entity.CompanionReview;
import com.feedback.Exceptions.FeedbackAlreadySubmittedException;
import com.feedback.Exceptions.InvalidCompanionReviewException;
import com.feedback.Exceptions.ReviewLimitExceededException;
import com.feedback.Repository.AppFeedbackRepo;
import com.feedback.Repository.CompanionReviewRepo;
import com.feedback.Service.FeedbackDomainEventPublisher;
import com.feedback.Service.NotificationProducer;
import com.feedback.ServiceImpl.FeedbackServiceImpl;

@ExtendWith(MockitoExtension.class)
class FeedbackServiceTest {

    @Mock
    private CompanionReviewRepo companionReviewRepo;

    @Mock
    private AppFeedbackRepo appFeedbackRepo;

    @Mock
    private TripClient tripClient;

    @Mock
    private NotificationProducer notificationProducer;

    @Mock
    private FeedbackDomainEventPublisher feedbackDomainEventPublisher;

    @Captor
    private ArgumentCaptor<Pageable> pageableCaptor;

    @Captor
    private ArgumentCaptor<CompanionReview> companionReviewCaptor;

    @Captor
    private ArgumentCaptor<AppFeedback> appFeedbackCaptor;

    private FeedbackServiceImpl feedbackService;

    @BeforeEach
    void setUp() {
        feedbackService = new FeedbackServiceImpl(
                companionReviewRepo,
                appFeedbackRepo,
                tripClient,
                notificationProducer,
                feedbackDomainEventPublisher);

        ReflectionTestUtils.setField(feedbackService, "maxPeerReviewsPerTrip", 3);
        ReflectionTestUtils.setField(feedbackService, "maxPageSize", 100);
    }

    @Test
    void submitCompanionReview_shouldSucceed() {
        SubmitCompanionReviewRequest request = SubmitCompanionReviewRequest.builder()
                .tripId("trip-1")
                .reviewerUserId("user-1")
                .targetUserId("user-2")
                .rating(5)
                .review("Great companion")
                .tags(Set.of("friendly", "punctual"))
                .build();

        TripSummary tripSummary = TripSummary.builder()
                .tripId("trip-1")
                .tripName("Goa Trip")
                .members(Set.of("user-1", "user-2", "user-3"))
                .build();

        CompanionReview saved = CompanionReview.builder()
                .reviewId("review-1")
                .tripId("trip-1")
                .reviewerUserId("user-1")
                .targetUserId("user-2")
                .rating(5)
                .review("Great companion")
                .tags(Set.of("friendly", "punctual"))
                .createdAt(Instant.now())
                .build();

        when(tripClient.getTripSummaryById("trip-1")).thenReturn(tripSummary);
        when(companionReviewRepo.findByTripIdAndReviewerUserIdAndTargetUserId("trip-1", "user-1", "user-2"))
                .thenReturn(Optional.empty());
        when(companionReviewRepo.countByTripIdAndReviewerUserId("trip-1", "user-1")).thenReturn(0L);
        when(companionReviewRepo.save(any(CompanionReview.class))).thenReturn(saved);

        CompanionReviewResponse response = feedbackService.submitCompanionReview(request);

        assertNotNull(response);
        assertEquals("review-1", response.getReviewId());
        assertEquals("trip-1", response.getTripId());
        assertEquals("user-1", response.getReviewerUserId());
        assertEquals("user-2", response.getTargetUserId());

        verify(companionReviewRepo).save(companionReviewCaptor.capture());
        CompanionReview toSave = companionReviewCaptor.getValue();
        assertEquals("trip-1", toSave.getTripId());
        assertEquals("user-1", toSave.getReviewerUserId());
        assertEquals("user-2", toSave.getTargetUserId());
        assertEquals(Set.of("friendly", "punctual"), toSave.getTags());

        verify(feedbackDomainEventPublisher).publishCompanionReviewSubmitted(saved);
        verify(notificationProducer).sendCompanionReviewNotification("user-1", "user-2", "trip-1", "Goa Trip");
    }

    @Test
    void submitCompanionReview_shouldFail_whenReviewerTargetsSelf() {
        SubmitCompanionReviewRequest request = SubmitCompanionReviewRequest.builder()
                .tripId("trip-1")
                .reviewerUserId("user-1")
                .targetUserId("user-1")
                .rating(5)
                .review("self")
                .build();

        InvalidCompanionReviewException ex = assertThrows(
                InvalidCompanionReviewException.class,
                () -> feedbackService.submitCompanionReview(request));

        assertEquals("You cannot submit companion review for yourself.", ex.getMessage());
        verifyNoInteractions(tripClient, companionReviewRepo, feedbackDomainEventPublisher, notificationProducer);
    }

    @Test
    void submitCompanionReview_shouldFail_whenTripMembersUnavailable() {
        SubmitCompanionReviewRequest request = SubmitCompanionReviewRequest.builder()
                .tripId("trip-1")
                .reviewerUserId("user-1")
                .targetUserId("user-2")
                .rating(4)
                .review("nice")
                .build();

        when(tripClient.getTripSummaryById("trip-1")).thenReturn(TripSummary.builder().tripId("trip-1").tripName("Goa").build());

        InvalidCompanionReviewException ex = assertThrows(
                InvalidCompanionReviewException.class,
                () -> feedbackService.submitCompanionReview(request));

        assertEquals("Trip members data not available.", ex.getMessage());
        verify(companionReviewRepo, never()).save(any());
    }

    @Test
    void submitCompanionReview_shouldFail_whenReviewerIsNotMember() {
        SubmitCompanionReviewRequest request = SubmitCompanionReviewRequest.builder()
                .tripId("trip-1")
                .reviewerUserId("user-1")
                .targetUserId("user-2")
                .rating(4)
                .review("nice")
                .build();

        when(tripClient.getTripSummaryById("trip-1")).thenReturn(TripSummary.builder()
                .tripId("trip-1")
                .tripName("Goa")
                .members(Set.of("user-2", "user-3"))
                .build());

        InvalidCompanionReviewException ex = assertThrows(
                InvalidCompanionReviewException.class,
                () -> feedbackService.submitCompanionReview(request));

        assertEquals("Reviewer is not a trip member.", ex.getMessage());
        verify(companionReviewRepo, never()).save(any());
    }

    @Test
    void submitCompanionReview_shouldFail_whenTargetIsNotMember() {
        SubmitCompanionReviewRequest request = SubmitCompanionReviewRequest.builder()
                .tripId("trip-1")
                .reviewerUserId("user-1")
                .targetUserId("user-2")
                .rating(4)
                .review("nice")
                .build();

        when(tripClient.getTripSummaryById("trip-1")).thenReturn(TripSummary.builder()
                .tripId("trip-1")
                .tripName("Goa")
                .members(Set.of("user-1", "user-3"))
                .build());

        InvalidCompanionReviewException ex = assertThrows(
                InvalidCompanionReviewException.class,
                () -> feedbackService.submitCompanionReview(request));

        assertEquals("Target user is not a trip member.", ex.getMessage());
        verify(companionReviewRepo, never()).save(any());
    }

    @Test
    void submitCompanionReview_shouldFail_whenDuplicateReviewExists() {
        SubmitCompanionReviewRequest request = SubmitCompanionReviewRequest.builder()
                .tripId("trip-1")
                .reviewerUserId("user-1")
                .targetUserId("user-2")
                .rating(5)
                .review("dup")
                .build();

        when(tripClient.getTripSummaryById("trip-1")).thenReturn(TripSummary.builder()
                .tripId("trip-1")
                .tripName("Goa")
                .members(Set.of("user-1", "user-2"))
                .build());

        when(companionReviewRepo.findByTripIdAndReviewerUserIdAndTargetUserId("trip-1", "user-1", "user-2"))
                .thenReturn(Optional.of(CompanionReview.builder().reviewId("existing").build()));

        FeedbackAlreadySubmittedException ex = assertThrows(
                FeedbackAlreadySubmittedException.class,
                () -> feedbackService.submitCompanionReview(request));

        assertEquals("Companion review already submitted for this user in this trip.", ex.getMessage());
        verify(companionReviewRepo, never()).save(any());
        verify(companionReviewRepo, never()).countByTripIdAndReviewerUserId(any(), any());
        verifyNoInteractions(feedbackDomainEventPublisher, notificationProducer);
    }

    @Test
    void submitCompanionReview_shouldFail_whenReviewLimitReached() {
        SubmitCompanionReviewRequest request = SubmitCompanionReviewRequest.builder()
                .tripId("trip-1")
                .reviewerUserId("user-1")
                .targetUserId("user-2")
                .rating(5)
                .review("limit")
                .build();

        when(tripClient.getTripSummaryById("trip-1")).thenReturn(TripSummary.builder()
                .tripId("trip-1")
                .tripName("Goa")
                .members(Set.of("user-1", "user-2", "user-3"))
                .build());

        when(companionReviewRepo.findByTripIdAndReviewerUserIdAndTargetUserId("trip-1", "user-1", "user-2"))
                .thenReturn(Optional.empty());
        when(companionReviewRepo.countByTripIdAndReviewerUserId("trip-1", "user-1")).thenReturn(3L);

        ReviewLimitExceededException ex = assertThrows(
                ReviewLimitExceededException.class,
                () -> feedbackService.submitCompanionReview(request));

        assertEquals("Review limit reached. You can submit at most 3 companion reviews per trip.", ex.getMessage());
        verify(companionReviewRepo, never()).save(any());
        verifyNoInteractions(feedbackDomainEventPublisher, notificationProducer);
    }

    @Test
    void getTripCompanionReviews_shouldUseTripOnlyQuery_whenTargetIsBlank() {
        CompanionReview review = CompanionReview.builder()
                .reviewId("review-1")
                .tripId("trip-1")
                .reviewerUserId("user-1")
                .targetUserId("user-2")
                .rating(5)
                .review("great")
                .createdAt(Instant.now())
                .build();

        Page<CompanionReview> reviewPage = new PageImpl<>(
                List.of(review),
                PageRequest.of(0, 100, Sort.by(Sort.Direction.DESC, "createdAt")),
                1);

        when(companionReviewRepo.findByTripId(eq("trip-1"), any(Pageable.class))).thenReturn(reviewPage);

        PageResponseDto<CompanionReviewResponse> response = feedbackService.getTripCompanionReviews(
                "trip-1", " ", -7, 500, null, "invalid");

        assertEquals(1, response.getItems().size());
        assertEquals("review-1", response.getItems().get(0).getReviewId());

        verify(companionReviewRepo).findByTripId(eq("trip-1"), pageableCaptor.capture());
        Pageable pageableUsed = pageableCaptor.getValue();
        assertEquals(0, pageableUsed.getPageNumber());
        assertEquals(100, pageableUsed.getPageSize());
        assertEquals(Sort.Direction.DESC, pageableUsed.getSort().getOrderFor("createdAt").getDirection());
        verify(companionReviewRepo, never()).findByTripIdAndTargetUserId(any(), any(), any());
    }

    @Test
    void getTripCompanionReviews_shouldUseTargetQuery_whenTargetIsProvided() {
        Page<CompanionReview> reviewPage = new PageImpl<>(
                List.of(),
                PageRequest.of(1, 20, Sort.by(Sort.Direction.ASC, "rating")),
                0);

        when(companionReviewRepo.findByTripIdAndTargetUserId(eq("trip-1"), eq("user-2"), any(Pageable.class)))
                .thenReturn(reviewPage);

        feedbackService.getTripCompanionReviews("trip-1", "user-2", 1, 20, "rating", "asc");

        verify(companionReviewRepo).findByTripIdAndTargetUserId(eq("trip-1"), eq("user-2"), pageableCaptor.capture());
        Pageable pageableUsed = pageableCaptor.getValue();
        assertEquals(1, pageableUsed.getPageNumber());
        assertEquals(20, pageableUsed.getPageSize());
        assertEquals(Sort.Direction.ASC, pageableUsed.getSort().getOrderFor("rating").getDirection());
        verify(companionReviewRepo, never()).findByTripId(any(), any());
    }

    @Test
    void hasUserAlreadySubmittedCompanionReview_shouldReturnTrue_whenPresent() {
        when(companionReviewRepo.findByTripIdAndReviewerUserIdAndTargetUserId("trip-1", "user-1", "user-2"))
                .thenReturn(Optional.of(CompanionReview.builder().reviewId("r1").build()));

        boolean result = feedbackService.hasUserAlreadySubmittedCompanionReview("trip-1", "user-1", "user-2");

        assertTrue(result);
    }

    @Test
    void hasUserAlreadySubmittedCompanionReview_shouldReturnFalse_whenAbsent() {
        when(companionReviewRepo.findByTripIdAndReviewerUserIdAndTargetUserId("trip-1", "user-1", "user-2"))
                .thenReturn(Optional.empty());

        boolean result = feedbackService.hasUserAlreadySubmittedCompanionReview("trip-1", "user-1", "user-2");

        assertFalse(result);
    }

    @Test
    void getSuggestedCompanionsForReview_shouldReturnEmpty_whenTripMembersMissing() {
        when(tripClient.getTripSummaryById("trip-1")).thenReturn(null);

        SuggestedCompanionResponse response = feedbackService.getSuggestedCompanionsForReview("trip-1", "user-1", 4);

        assertEquals("trip-1", response.getTripId());
        assertEquals("user-1", response.getReviewerUserId());
        assertEquals(3, response.getMaxReviewsAllowed());
        assertEquals(0, response.getReviewsSubmitted());
        assertEquals(3, response.getReviewsRemaining());
        assertTrue(response.getSuggestedTargetUserIds().isEmpty());
    }

    @Test
    void getSuggestedCompanionsForReview_shouldFilterReviewedSelfAndRespectRemainingLimit() {
        when(tripClient.getTripSummaryById("trip-1")).thenReturn(TripSummary.builder()
                .tripId("trip-1")
                .tripName("Goa")
                .members(Set.of("user-1", "user-2", "user-3", "user-4", "user-5"))
                .build());

        when(companionReviewRepo.findByTripIdAndReviewerUserId("trip-1", "user-1")).thenReturn(List.of(
                CompanionReview.builder().targetUserId("user-2").build(),
                CompanionReview.builder().targetUserId("user-3").build()));

        SuggestedCompanionResponse response = feedbackService.getSuggestedCompanionsForReview("trip-1", "user-1", 0);

        assertEquals(2, response.getReviewsSubmitted());
        assertEquals(1, response.getReviewsRemaining());
        assertEquals(1, response.getSuggestedTargetUserIds().size());
        assertTrue(response.getSuggestedTargetUserIds().contains("user-4") || response.getSuggestedTargetUserIds().contains("user-5"));
    }

    @Test
    void submitAppFeedback_shouldSucceed_andPublishEvent() {
        SubmitAppFeedbackRequest request = SubmitAppFeedbackRequest.builder()
                .userId("user-1")
                .tripId("trip-1")
                .rating(5)
                .comment("Very good app")
                .featuresLiked(null)
                .build();

        AppFeedback saved = AppFeedback.builder()
                .feedbackId("app-fb-1")
                .userId("user-1")
                .tripId("trip-1")
                .rating(5)
                .comment("Very good app")
                .featuresLiked(Set.of())
                .createdAt(Instant.now())
                .build();

        when(appFeedbackRepo.save(any(AppFeedback.class))).thenReturn(saved);

        AppFeedbackResponse response = feedbackService.submitAppFeedback(request);

        assertNotNull(response);
        assertEquals("app-fb-1", response.getFeedbackId());
        assertEquals("user-1", response.getUserId());
        assertEquals(5, response.getRating());

        verify(appFeedbackRepo).save(appFeedbackCaptor.capture());
        AppFeedback toSave = appFeedbackCaptor.getValue();
        assertNotNull(toSave.getFeaturesLiked());
        assertTrue(toSave.getFeaturesLiked().isEmpty());

        verify(feedbackDomainEventPublisher).publishAppFeedbackSubmitted(saved);
    }

    @Test
    void getAppFeedbackByUser_shouldReturnPage_andNormalizePageable() {
        AppFeedback feedback = AppFeedback.builder()
                .feedbackId("fb-1")
                .userId("user-1")
                .tripId("trip-1")
                .rating(4)
                .comment("good")
                .createdAt(Instant.now())
                .build();

        Page<AppFeedback> feedbackPage = new PageImpl<>(
                List.of(feedback),
                PageRequest.of(0, 100, Sort.by(Sort.Direction.DESC, "createdAt")),
                1);

        when(appFeedbackRepo.findByUserId(eq("user-1"), any(Pageable.class))).thenReturn(feedbackPage);

        PageResponseDto<AppFeedbackResponse> response = feedbackService.getAppFeedbackByUser("user-1", -10, 500);

        assertEquals(1, response.getItems().size());
        assertEquals("fb-1", response.getItems().get(0).getFeedbackId());

        verify(appFeedbackRepo).findByUserId(eq("user-1"), pageableCaptor.capture());
        Pageable pageableUsed = pageableCaptor.getValue();
        assertEquals(0, pageableUsed.getPageNumber());
        assertEquals(100, pageableUsed.getPageSize());
        assertEquals(Sort.Direction.DESC, pageableUsed.getSort().getOrderFor("createdAt").getDirection());
    }
}

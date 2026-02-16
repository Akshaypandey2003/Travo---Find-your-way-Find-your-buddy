package com.feedback.UnitTests;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.feedback.Clients.TripClient;
import com.feedback.DTO.TripSummary;
import com.feedback.Entity.FeedBack;
import com.feedback.Repository.FeedbackRepo;
import com.feedback.Service.NotificationProducer;
import com.feedback.ServiceImpl.FeedbackServiceImpl;

@ExtendWith(MockitoExtension.class)
class FeedbackServiceTest {

    @Mock
    private FeedbackRepo feedbackRepo;

    @Mock
    private TripClient tripClient;

    @Mock
    private NotificationProducer notificationProducer;

    @InjectMocks
    private FeedbackServiceImpl feedbackService; // ✅ REAL SERVICE

    private FeedBack feedback;
    private TripSummary tripSummary;

    @BeforeEach
    void setup() {
        feedback = new FeedBack();
        feedback.setTripId("trip123");
        feedback.setAuthorId("user1");
        feedback.setComment("Great trip!");

        tripSummary = new TripSummary();
        tripSummary.setTripId("trip123");
        tripSummary.setTripName("Goa Trip");
        tripSummary.setMembers(Set.of("user2", "user3"));
    }

    // ---------------- submitFeedback ----------------

    @Test
    void submitFeedback_success() {
        when(feedbackRepo.findByTripIdAndAuthorId("trip123", "user1"))
                .thenReturn(Optional.empty());

        when(tripClient.getTripSummaryById("trip123"))
                .thenReturn(tripSummary);

        when(feedbackRepo.save(feedback))
                .thenReturn(feedback);

        FeedBack savedFeedback = feedbackService.submitFeedback(feedback);

        assertNotNull(savedFeedback);
        assertEquals("trip123", savedFeedback.getTripId());

        verify(tripClient).getTripSummaryById("trip123");
        verify(notificationProducer, times(2))
                .sendFeedbackNotification(
                        eq("user1"),
                        anyString(),
                        eq("trip123"),
                        eq("Goa Trip")
                );
        verify(feedbackRepo).save(feedback);
    }

    @Test
    void submitFeedback_shouldThrowException_ifAlreadySubmitted() {
        when(feedbackRepo.findByTripIdAndAuthorId("trip123", "user1"))
                .thenReturn(Optional.of(feedback));

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> feedbackService.submitFeedback(feedback)
        );

        assertEquals(
                "Feedback already submitted for this trip by the user.",
                exception.getMessage()
        );

        verify(tripClient, never()).getTripSummaryById(any());
        verify(notificationProducer, never()).sendFeedbackNotification(any(), any(), any(), any());
        verify(feedbackRepo, never()).save(any());
    }

    @Test
    void submitFeedback_shouldSendNotificationToEachMember() {
        when(feedbackRepo.findByTripIdAndAuthorId(any(), any()))
                .thenReturn(Optional.empty());

        when(tripClient.getTripSummaryById(any()))
                .thenReturn(tripSummary);

        when(feedbackRepo.save(any()))
                .thenReturn(feedback);

        feedbackService.submitFeedback(feedback);

        for (String member : tripSummary.getMembers()) {
            verify(notificationProducer).sendFeedbackNotification(
                    "user1",
                    member,
                    "trip123",
                    "Goa Trip"
            );
        }
    }

    // ---------------- getFeedback ----------------

    @Test
    void getFeedback_shouldReturnFeedbackList() {
        List<FeedBack> feedbackList = List.of(feedback);

        when(feedbackRepo.findAllByTripId("trip123"))
                .thenReturn(feedbackList);

        List<FeedBack> result = feedbackService.getFeedback("trip123");

        assertEquals(1, result.size());
        verify(feedbackRepo).findAllByTripId("trip123");
    }

    // ---------------- hasUserAlreadySubmitted ----------------

    @Test
    void hasUserAlreadySubmitted_shouldReturnTrue() {
        when(feedbackRepo.findByTripIdAndAuthorId("trip123", "user1"))
                .thenReturn(Optional.of(feedback));

        boolean result = feedbackService.hasUserAlreadySubmitted("trip123", "user1");

        assertTrue(result);
    }

    @Test
    void hasUserAlreadySubmitted_shouldReturnFalse() {
        when(feedbackRepo.findByTripIdAndAuthorId("trip123", "user1"))
                .thenReturn(Optional.empty());

        boolean result = feedbackService.hasUserAlreadySubmitted("trip123", "user1");

        assertFalse(result);
    }
}

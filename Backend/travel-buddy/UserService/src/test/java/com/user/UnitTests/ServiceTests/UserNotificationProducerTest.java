package com.user.UnitTests.ServiceTests;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.concurrent.CompletableFuture;

import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

import com.events.Notification.FailedNotification;
import com.events.Notification.NotificationEvent;
import com.events.Repositories.FailedNotificationRepository;
import com.user.ServiceImpl.UserNotificationProducer;

@ExtendWith(MockitoExtension.class)
public class UserNotificationProducerTest {

    @Mock
    private KafkaTemplate<String, NotificationEvent> kafkaTemplate;

    @Mock
    private FailedNotificationRepository failedNotificationRepo;

    @InjectMocks
    private UserNotificationProducer producer;

    private CompletableFuture<SendResult<String, NotificationEvent>> successFuture;

    @BeforeEach
    void setup() {

        // Create mocked metadata
        RecordMetadata metadata = mock(RecordMetadata.class);

        // Create ProducerRecord
        ProducerRecord<String, NotificationEvent> producerRecord =
                new ProducerRecord<>("notification-events", "receiver", mock(NotificationEvent.class));

        // Create SendResult
        SendResult<String, NotificationEvent> sendResult =
                new SendResult<>(producerRecord, metadata);

        // IMPORTANT: completed future
        successFuture = CompletableFuture.completedFuture(sendResult);
    }

    // =====================================================
    // FRIEND REQUEST SEND SUCCESS
    // =====================================================

    @Test
    void friendRequestSend_Success() {

        when(kafkaTemplate.send(anyString(), anyString(), any()))
                .thenReturn(successFuture);

        producer.friendRequestSend("sender1", "Akshay", "receiver1");

        verify(kafkaTemplate, times(1))
                .send(eq("notification-events"), eq("receiver1"), any());

        verify(failedNotificationRepo, never())
                .save(any());
    }

    // =====================================================
    // FRIEND REQUEST SEND FAILURE
    // =====================================================

    @Test
    void friendRequestSend_Failure_ShouldThrowException() {

        CompletableFuture<SendResult<String, NotificationEvent>> failedFuture =
                new CompletableFuture<>();

        failedFuture.completeExceptionally(
                new RuntimeException("Kafka failure"));

        when(kafkaTemplate.send(anyString(), anyString(), any()))
                .thenReturn(failedFuture);

        assertThrows(RuntimeException.class,
                () -> producer.friendRequestSend("sender","Akshay", "receiver"));
    }

    // =====================================================
    // FRIEND REQUEST FALLBACK
    // =====================================================

    @Test
    void friendRequestFallback_ShouldSaveFailedNotification() {

        producer.sendFriendRequestFallback(
                "sender",
                "Akshay",
                "receiver",
                new RuntimeException("Kafka down"));

        verify(failedNotificationRepo, times(1))
                .save(any(FailedNotification.class));
    }

    // =====================================================
    // FRIEND REQUEST ACCEPT SUCCESS
    // =====================================================

    @Test
    void friendRequestAccept_Success() {

        when(kafkaTemplate.send(anyString(), anyString(), any()))
                .thenReturn(successFuture);

        producer.friendRequestAccept("sender", "Akhil", "receiver");

        verify(kafkaTemplate).send(anyString(), anyString(), any());

        verify(failedNotificationRepo, never()).save(any());
    }

    // =====================================================
    // FRIEND REQUEST ACCEPT FALLBACK
    // =====================================================

    @Test
    void acceptFriendRequestFallback_ShouldSaveFailedNotification() {

        producer.acceptFriendRequestFallback(
                "sender",
                "Akhil",
                "receiver",
                new RuntimeException("Kafka error"));

        verify(failedNotificationRepo).save(any());
    }

    // =====================================================
    // PASSWORD RESET SUCCESS
    // =====================================================

    @Test
    void sendPasswordResetNotification_Success() {

        when(kafkaTemplate.send(anyString(), anyString(), any()))
                .thenReturn(successFuture);

        producer.sendPasswordResetNotification(
                "user1",
                "test@email.com",
                "Akshay",
                "resetLink");

        verify(kafkaTemplate).send(anyString(), anyString(), any());

        verify(failedNotificationRepo, never()).save(any());
    }

    // =====================================================
    // PASSWORD RESET FALLBACK
    // =====================================================

    @Test
    void resetPasswordFallback_ShouldSaveFailedNotification() {

        producer.resetPasswordFallback(
                "user1",
                "email",
                "Akshay",
                "link",
                new RuntimeException("Kafka error"));

        verify(failedNotificationRepo).save(any());
    }

    // =====================================================
    // WELCOME SUCCESS
    // =====================================================

    @Test
    void sendWelcomeNotification_Success() {

        when(kafkaTemplate.send(anyString(), anyString(), any()))
                .thenReturn(successFuture);

        producer.sendWelcomeNotification(
                "user1",
                "Akshay",
                "email");

        verify(kafkaTemplate).send(anyString(), anyString(), any());

        verify(failedNotificationRepo, never()).save(any());
    }

    // =====================================================
    // WELCOME FALLBACK
    // =====================================================

    @Test
    void welcomeFallback_ShouldSaveFailedNotification() {

        producer.welcomeFallback(
                "user1",
                "Akshay",
                "email",
                new RuntimeException("Kafka down"));

        verify(failedNotificationRepo).save(any());
    }

    // =====================================================
    // PASSWORD RESET SUCCESS NOTIFICATION SUCCESS
    // =====================================================

    @Test
    void sendPasswordResetSuccessNotification_Success() {

        when(kafkaTemplate.send(anyString(), anyString(), any()))
                .thenReturn(successFuture);

        producer.sendPasswordResetSuccessNotification(
                "user1",
                "Akshay",
                "email");

        verify(kafkaTemplate).send(anyString(), anyString(), any());

        verify(failedNotificationRepo, never()).save(any());
    }

    // =====================================================
    // PASSWORD RESET SUCCESS FALLBACK
    // =====================================================

    @Test
    void resetSuccessFallback_ShouldSaveFailedNotification() {

        producer.resetSuccessFallback(
                "user1",
                "Akshay",
                "email",
                new RuntimeException("Kafka down"));

        verify(failedNotificationRepo).save(any());
    }
}

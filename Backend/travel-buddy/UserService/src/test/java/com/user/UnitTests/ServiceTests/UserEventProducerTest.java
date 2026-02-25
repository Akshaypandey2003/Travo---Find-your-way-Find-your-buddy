package com.user.UnitTests.ServiceTests;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.Map;
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
import com.events.Repositories.FailedNotificationRepository;
import com.user.ServiceImpl.UserEventProducer;

@ExtendWith(MockitoExtension.class)
public class UserEventProducerTest {

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Mock
    private FailedNotificationRepository failedNotificationRepo;

    @InjectMocks
    private UserEventProducer producer;

    private CompletableFuture<SendResult<String, Object>> successFuture;

    @BeforeEach
    void setup() {

        RecordMetadata metadata = mock(RecordMetadata.class);

        ProducerRecord<String, Object> producerRecord =
                new ProducerRecord<>("user-events", "user1", new Object());

        SendResult<String, Object> sendResult =
                new SendResult<>(producerRecord, metadata);

        successFuture = CompletableFuture.completedFuture(sendResult);
    }

    // =====================================================
    // USER CREATED SUCCESS
    // =====================================================

    @Test
    void publishUserCreated_Success() {

        when(kafkaTemplate.send(anyString(), anyString(), any()))
                .thenReturn(successFuture);

        producer.publishUserCreated("user1", "Akshay", "test@email.com");

        verify(kafkaTemplate).send(eq("user-events"), eq("user1"), any());
        verify(failedNotificationRepo, never()).save(any());
    }

    // =====================================================
    // USER CREATED FAILURE
    // =====================================================

    @Test
    void publishUserCreated_Failure_ShouldThrowException() {

        CompletableFuture<SendResult<String, Object>> failedFuture =
                new CompletableFuture<>();

        failedFuture.completeExceptionally(new RuntimeException("Kafka down"));

        when(kafkaTemplate.send(anyString(), anyString(), any()))
                .thenReturn(failedFuture);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> producer.publishUserCreated("user1", "Akshay", "email"));

        assertTrue(ex.getMessage().contains("Kafka down"));

    }

    // =====================================================
    // USER CREATED FALLBACK
    // =====================================================

    @Test
    void publishUserCreatedFallback_ShouldSaveFailedEvent() {

        producer.publishUserCreatedFallback(
                "user1",
                "Akshay",
                "email",
                new RuntimeException("Kafka down"));

        verify(failedNotificationRepo).save(any(FailedNotification.class));
    }

    // =====================================================
    // USER DELETED SUCCESS
    // =====================================================

    @Test
    void publishUserDeleted_Success() {

        when(kafkaTemplate.send(anyString(), anyString(), any()))
                .thenReturn(successFuture);

        producer.publishUserDeleted("user1");

        verify(kafkaTemplate).send(eq("user-events"), eq("user1"), any());
        verify(failedNotificationRepo, never()).save(any());
    }

    // =====================================================
    // USER DELETED FAILURE
    // =====================================================

    @Test
    void publishUserDeleted_Failure_ShouldThrowException() {

        CompletableFuture<SendResult<String, Object>> failedFuture =
                new CompletableFuture<>();

        failedFuture.completeExceptionally(new RuntimeException("Kafka down"));

        when(kafkaTemplate.send(anyString(), anyString(), any()))
                .thenReturn(failedFuture);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> producer.publishUserDeleted("user1"));

        assertTrue(ex.getMessage().contains("Kafka down"));
    }

    // =====================================================
    // USER DELETED FALLBACK
    // =====================================================

    @Test
    void publishUserDeletedFallback_ShouldSaveFailedEvent() {

        producer.publishUserDeletedFallback(
                "user1",
                new RuntimeException("Kafka down"));

        verify(failedNotificationRepo).save(any(FailedNotification.class));
    }

    // =====================================================
    // USER UPDATED SUCCESS
    // =====================================================

    @Test
    void publishUserUpdated_Success() {

        when(kafkaTemplate.send(anyString(), anyString(), any()))
                .thenReturn(successFuture);

        Map<String, Object> updatedFields = new HashMap<>();
        updatedFields.put("username", "NewName");

        producer.publishUserUpdated("user1", updatedFields);

        verify(kafkaTemplate).send(eq("user-events"), eq("user1"), any());
        verify(failedNotificationRepo, never()).save(any());
    }

    // =====================================================
    // USER UPDATED FAILURE
    // =====================================================

    @Test
    void publishUserUpdated_Failure_ShouldThrowException() {

        when(kafkaTemplate.send(anyString(), anyString(), any()))
                .thenThrow(new RuntimeException("Kafka down"));

        Map<String, Object> updatedFields = new HashMap<>();

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> producer.publishUserUpdated("user1", updatedFields));

        assertEquals("Kafka down", ex.getMessage());
    }

    // =====================================================
    // USER UPDATED FALLBACK
    // =====================================================

    @Test
    void publishUserUpdatedFallback_ShouldSaveFailedEvent() {

        Map<String, Object> updatedFields = new HashMap<>();
        updatedFields.put("email", "new@email.com");

        producer.publishUserUpdatedFallback(
                "user1",
                updatedFields,
                new RuntimeException("Kafka down"));

        verify(failedNotificationRepo).save(any(FailedNotification.class));
    }
}

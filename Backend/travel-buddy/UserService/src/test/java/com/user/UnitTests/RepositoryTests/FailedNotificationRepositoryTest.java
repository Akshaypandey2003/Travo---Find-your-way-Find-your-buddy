package com.user.UnitTests.RepositoryTests;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.events.Notification.FailedNotification;
import com.events.Repositories.FailedNotificationRepository;


@DataMongoTest
@Testcontainers
@ActiveProfiles("test")
class FailedNotificationRepositoryTest {

    @Container
    static MongoDBContainer mongo = new MongoDBContainer("mongo:7.0");

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongo::getReplicaSetUrl);
    }

    @Autowired
    FailedNotificationRepository failedNotificationRepo;

    @BeforeEach
    void setUp() {
        failedNotificationRepo.deleteAll();
    }

    @Test
    void findAllByOrderByCreatedAtAsc_shouldReturnAscendingOrder() {
        failedNotificationRepo.save(notification("topic-a", "k1", 3_000L));
        failedNotificationRepo.save(notification("topic-b", "k2", 1_000L));
        failedNotificationRepo.save(notification("topic-c", "k3", 2_000L));

        Page<FailedNotification> page = failedNotificationRepo.findAllByOrderByCreatedAtAsc(PageRequest.of(0, 10));

        assertThat(page.getContent()).extracting(FailedNotification::getCreatedAt)
                .containsExactly(1_000L, 2_000L, 3_000L);
    }

    @Test
    void findAllByOrderByCreatedAtAsc_shouldSupportPagination() {
        failedNotificationRepo.save(notification("topic-a", "k1", 1_000L));
        failedNotificationRepo.save(notification("topic-b", "k2", 2_000L));
        failedNotificationRepo.save(notification("topic-c", "k3", 3_000L));

        Page<FailedNotification> page = failedNotificationRepo.findAllByOrderByCreatedAtAsc(PageRequest.of(0, 2));

        assertThat(page.getContent()).hasSize(2);
        assertThat(page.getTotalElements()).isEqualTo(3);
        assertThat(page.getContent()).extracting(FailedNotification::getCreatedAt)
                .containsExactly(1_000L, 2_000L);
    }

    @Test
    void findAllByOrderByCreatedAtAsc_shouldReturnEmptyPage_whenNoRecords() {
        Page<FailedNotification> page = failedNotificationRepo.findAllByOrderByCreatedAtAsc(PageRequest.of(0, 10));
        assertThat(page.getContent()).isEmpty();
        assertThat(page.getTotalElements()).isZero();
    }

    private FailedNotification notification(String topic, String key, long createdAt) {
        return FailedNotification.builder()
                .topic(topic)
                .key(key)
                .event(Map.of("value", topic))
                .retryCount(0)
                .createdAt(createdAt)
                .build();
    }
}

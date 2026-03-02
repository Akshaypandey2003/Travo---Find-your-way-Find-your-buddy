package com.user.UnitTests.RepositoryTests;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
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

import com.user.Entity.Connections;
import com.user.Enum.ConnectionStatus;
import com.user.Repository.ConnectionRepo;

@DataMongoTest
@EnabledIfSystemProperty(named = "runDockerTests", matches = "true")
@Testcontainers
@ActiveProfiles("test")
class ConnectionRepositoryTest {

    @Container
    static MongoDBContainer mongo = new MongoDBContainer("mongo:7.0");

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongo::getReplicaSetUrl);
    }

    @Autowired
    ConnectionRepo connectionRepo;

    @BeforeEach
    void setUp() {
        connectionRepo.deleteAll();
    }

    @Test
    void findByFollowerIdAndFollowingId_shouldReturnConnection_whenExists() {
        connectionRepo.save(connection("u1", "u2", ConnectionStatus.FOLLOWING));

        assertThat(connectionRepo.findByFollowerIdAndFollowingId("u1", "u2"))
                .isPresent()
                .get()
                .extracting(Connections::getStatus)
                .isEqualTo(ConnectionStatus.FOLLOWING);
    }

    @Test
    void findByFollowerIdAndFollowingId_shouldReturnEmpty_whenNotExists() {
        assertThat(connectionRepo.findByFollowerIdAndFollowingId("none", "none")).isEmpty();
    }

    @Test
    void findByFollowingIdAndStatus_and_findByFollowerIdAndStatus_shouldPageCorrectly() {
        connectionRepo.save(connection("u1", "u3", ConnectionStatus.FOLLOWING));
        connectionRepo.save(connection("u2", "u3", ConnectionStatus.FOLLOWING));
        connectionRepo.save(connection("u4", "u3", ConnectionStatus.REQUESTED));

        Page<Connections> followersOfU3 = connectionRepo.findByFollowingIdAndStatus(
                "u3", ConnectionStatus.FOLLOWING, PageRequest.of(0, 10));

        assertThat(followersOfU3.getTotalElements()).isEqualTo(2);
        assertThat(followersOfU3.getContent()).extracting(Connections::getFollowerId)
                .containsExactlyInAnyOrder("u1", "u2");

        Page<Connections> followingByU1 = connectionRepo.findByFollowerIdAndStatus(
                "u1", ConnectionStatus.FOLLOWING, PageRequest.of(0, 10));

        assertThat(followingByU1.getTotalElements()).isEqualTo(1);
        assertThat(followingByU1.getContent().get(0).getFollowingId()).isEqualTo("u3");
    }

    @Test
    void listFinders_shouldReturnExpectedRecords() {
        connectionRepo.save(connection("u1", "u2", ConnectionStatus.FOLLOWING));
        connectionRepo.save(connection("u1", "u3", ConnectionStatus.REQUESTED));
        connectionRepo.save(connection("u4", "u1", ConnectionStatus.FOLLOWING));

        List<Connections> byFollowerStatus = connectionRepo.findByFollowerIdAndStatus("u1", ConnectionStatus.FOLLOWING);
        assertThat(byFollowerStatus).hasSize(1);
        assertThat(byFollowerStatus.get(0).getFollowingId()).isEqualTo("u2");

        List<Connections> byFollowingStatus = connectionRepo.findByFollowingIdAndStatus("u1", ConnectionStatus.FOLLOWING);
        assertThat(byFollowingStatus).hasSize(1);
        assertThat(byFollowingStatus.get(0).getFollowerId()).isEqualTo("u4");
    }

    @Test
    void countAndExists_shouldWorkForMatchingAndNonMatchingCases() {
        connectionRepo.save(connection("u1", "u2", ConnectionStatus.FOLLOWING));
        connectionRepo.save(connection("u1", "u3", ConnectionStatus.REQUESTED));
        connectionRepo.save(connection("u4", "u2", ConnectionStatus.FOLLOWING));

        assertThat(connectionRepo.countByFollowerIdAndStatus("u1", ConnectionStatus.FOLLOWING)).isEqualTo(1);
        assertThat(connectionRepo.countByFollowingIdAndStatus("u2", ConnectionStatus.FOLLOWING)).isEqualTo(2);

        assertThat(connectionRepo.existsByFollowerIdAndFollowingIdAndStatus("u1", "u2", ConnectionStatus.FOLLOWING))
                .isTrue();
        assertThat(connectionRepo.existsByFollowerIdAndFollowingIdAndStatus("u1", "u2", ConnectionStatus.REQUESTED))
                .isFalse();
    }

    @Test
    void deleteByFollowerIdOrFollowingId_shouldDeleteMatchingRecordsOnly() {
        connectionRepo.save(connection("u1", "u2", ConnectionStatus.FOLLOWING));
        connectionRepo.save(connection("u3", "u1", ConnectionStatus.FOLLOWING));
        connectionRepo.save(connection("u4", "u5", ConnectionStatus.FOLLOWING));

        connectionRepo.deleteByFollowerIdOrFollowingId("u1", "u1");

        assertThat(connectionRepo.findByFollowerId("u1")).isEmpty();
        assertThat(connectionRepo.findByFollowingId("u1")).isEmpty();
        assertThat(connectionRepo.findByFollowerId("u4")).hasSize(1);
    }

    private Connections connection(String followerId, String followingId, ConnectionStatus status) {
        return Connections.builder()
                .followerId(followerId)
                .followingId(followingId)
                .status(status)
                .build();
    }
}

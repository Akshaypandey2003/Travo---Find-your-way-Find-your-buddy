package com.user.UnitTests.RepositoryTests;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.user.Entity.PasswordResetToken;
import com.user.Repository.PasswordResetTokenRepo;

@DataMongoTest
@EnabledIfSystemProperty(named = "runDockerTests", matches = "true")
@Testcontainers
@ActiveProfiles("test")
class PasswordResetTokenRepositoryTest {

    @Container
    static MongoDBContainer mongo = new MongoDBContainer("mongo:7.0");

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongo::getReplicaSetUrl);
    }

    @Autowired
    PasswordResetTokenRepo tokenRepo;

    @BeforeEach
    void setUp() {
        tokenRepo.deleteAll();
    }

    @Test
    void findByToken_shouldReturnToken_whenExists() {
        PasswordResetToken saved = tokenRepo.save(token("u1", false));

        assertThat(tokenRepo.findByToken(saved.getToken()))
                .isPresent()
                .get()
                .extracting(PasswordResetToken::getUserId)
                .isEqualTo("u1");
    }

    @Test
    void findByToken_shouldReturnEmpty_whenNotExists() {
        assertThat(tokenRepo.findByToken("missing-token")).isEmpty();
    }

    @Test
    void deleteByUserId_shouldDeleteAllTokensForUser_only() {
        PasswordResetToken t1 = tokenRepo.save(token("u1", false));
        PasswordResetToken t2 = tokenRepo.save(token("u1", true));
        PasswordResetToken t3 = tokenRepo.save(token("u2", false));

        tokenRepo.deleteByUserId("u1");

        assertThat(tokenRepo.findByToken(t1.getToken())).isEmpty();
        assertThat(tokenRepo.findByToken(t2.getToken())).isEmpty();
        assertThat(tokenRepo.findByToken(t3.getToken())).isPresent();
    }

    @Test
    void deleteByUserId_shouldDoNothing_whenUserHasNoTokens() {
        PasswordResetToken t1 = tokenRepo.save(token("u2", false));

        tokenRepo.deleteByUserId("u1");

        assertThat(tokenRepo.findByToken(t1.getToken())).isPresent();
    }

    private PasswordResetToken token(String userId, boolean used) {
        return PasswordResetToken.builder()
                .token(UUID.randomUUID().toString())
                .userId(userId)
                .expiryTime(System.currentTimeMillis() + 60_000)
                .used(used)
                .build();
    }
}

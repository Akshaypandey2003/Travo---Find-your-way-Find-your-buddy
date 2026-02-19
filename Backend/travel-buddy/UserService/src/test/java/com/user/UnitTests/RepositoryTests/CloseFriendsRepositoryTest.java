package com.user.UnitTests.RepositoryTests;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.user.Entity.CloseFriends;
import com.user.Repository.CloseFriendsRepo;

@DataMongoTest
@Testcontainers
@ActiveProfiles("test")
class CloseFriendsRepositoryTest {

    @Container
    static MongoDBContainer mongo = new MongoDBContainer("mongo:7.0");

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongo::getReplicaSetUrl);
    }

    @Autowired
    CloseFriendsRepo closeFriendsRepo;

    @BeforeEach
    void setUp() {
        closeFriendsRepo.deleteAll();
    }

    @Test
    void findByUserIdAndCloseFriendId_shouldReturnRelation_whenExists() {
        closeFriendsRepo.save(closeFriend("u1", "u2"));

        assertThat(closeFriendsRepo.findByUserIdAndCloseFriendId("u1", "u2"))
                .isPresent()
                .get()
                .extracting(CloseFriends::getUserId, CloseFriends::getCloseFriendId)
                .containsExactly("u1", "u2");
    }

    @Test
    void findByUserIdAndCloseFriendId_shouldReturnEmpty_whenNotExists() {
        assertThat(closeFriendsRepo.findByUserIdAndCloseFriendId("x", "y")).isEmpty();
    }

    @Test
    void findByUserId_and_countByUserId_shouldReturnExpectedResults() {
        closeFriendsRepo.save(closeFriend("u1", "u2"));
        closeFriendsRepo.save(closeFriend("u1", "u3"));
        closeFriendsRepo.save(closeFriend("u4", "u1"));

        List<CloseFriends> result = closeFriendsRepo.findByUserId("u1");
        assertThat(result).hasSize(2);
        assertThat(result).extracting(CloseFriends::getCloseFriendId)
                .containsExactlyInAnyOrder("u2", "u3");

        assertThat(closeFriendsRepo.countByUserId("u1")).isEqualTo(2);
        assertThat(closeFriendsRepo.countByUserId("missing")).isEqualTo(0);
    }

    @Test
    void findByCloseFriendId_shouldReturnUsersWhoAddedThatFriend() {
        closeFriendsRepo.save(closeFriend("u1", "u9"));
        closeFriendsRepo.save(closeFriend("u2", "u9"));
        closeFriendsRepo.save(closeFriend("u3", "u8"));

        List<CloseFriends> result = closeFriendsRepo.findByCloseFriendId("u9");
        assertThat(result).hasSize(2);
        assertThat(result).extracting(CloseFriends::getUserId)
                .containsExactlyInAnyOrder("u1", "u2");
    }

    @Test
    void deleteByUserIdAndCloseFriendId_shouldDeleteOnlyExactRelation() {
        closeFriendsRepo.save(closeFriend("u1", "u2"));
        closeFriendsRepo.save(closeFriend("u1", "u3"));

        closeFriendsRepo.deleteByUserIdAndCloseFriendId("u1", "u2");

        assertThat(closeFriendsRepo.findByUserIdAndCloseFriendId("u1", "u2")).isEmpty();
        assertThat(closeFriendsRepo.findByUserIdAndCloseFriendId("u1", "u3")).isPresent();
    }

    @Test
    void deleteByUserIdOrCloseFriendId_shouldDeleteMatchingRecords() {
        closeFriendsRepo.save(closeFriend("u1", "u2"));
        closeFriendsRepo.save(closeFriend("u3", "u1"));
        closeFriendsRepo.save(closeFriend("u4", "u5"));

        closeFriendsRepo.deleteByUserIdOrCloseFriendId("u1", "u1");

        assertThat(closeFriendsRepo.findByUserId("u1")).isEmpty();
        assertThat(closeFriendsRepo.findByCloseFriendId("u1")).isEmpty();
        assertThat(closeFriendsRepo.findByUserId("u4")).hasSize(1);
    }

    private CloseFriends closeFriend(String userId, String closeFriendId) {
        return CloseFriends.builder()
                .userId(userId)
                .closeFriendId(closeFriendId)
                .build();
    }
}

package com.user.UnitTests.RepositoryTests;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.user.Entity.User;
import com.user.Enum.AccountType;
import com.user.Repository.UserRepo;

@DataMongoTest
@EnabledIfSystemProperty(named = "runDockerTests", matches = "true")
@Testcontainers
@ActiveProfiles("test")
class UserRepositoryTest {

    @Container
    static MongoDBContainer mongo = new MongoDBContainer("mongo:7.0");

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongo::getReplicaSetUrl);
    }

    @Autowired
    UserRepo userRepo;

    @Autowired
    MongoTemplate mongoTemplate;

    @BeforeEach
    void setUp() {
        userRepo.deleteAll();
    }

    @Test
    void findByEmail_shouldReturnUser_whenEmailExists() {
        User saved = userRepo.save(buildUser("alice@gmail.com", List.of("Nature")));

        assertThat(userRepo.findByEmail("alice@gmail.com"))
                .isPresent()
                .get()
                .extracting(User::getUserId)
                .isEqualTo(saved.getUserId());
    }

    @Test
    void findByEmail_shouldReturnEmpty_whenEmailDoesNotExist() {
        assertThat(userRepo.findByEmail("missing@gmail.com")).isEmpty();
    }

    @Test
    void findByPreferencesInIgnoreCase_shouldReturnMatchingUsers() {
        userRepo.save(buildUser("u1@gmail.com", List.of("Nature", "Beach")));
        userRepo.save(buildUser("u2@gmail.com", List.of("Adventure")));
        userRepo.save(buildUser("u3@gmail.com", List.of("NATURE")));

        Page<User> page = userRepo.findByPreferencesInIgnoreCase(
                PageRequest.of(0, 10), List.of("nature"));

        assertThat(page.getContent()).extracting(User::getEmail)
                .contains("u1@gmail.com", "u3@gmail.com")
                .doesNotContain("u2@gmail.com");
    }

    @Test
    void counterUpdateMethods_shouldIncrementAndDecrementCounters() {
        User saved = userRepo.save(buildUser("counter@gmail.com", List.of("City")));
        String userId = saved.getUserId();

        userRepo.incrementFollowersCount(userId);
        userRepo.incrementFollowingCount(userId);
        userRepo.incrementCloseFriendsCount(userId);

        User afterIncrement = userRepo.findById(userId).orElseThrow();
        assertThat(afterIncrement.getFollowersCount()).isEqualTo(1);
        assertThat(afterIncrement.getFollowingsCount()).isEqualTo(1);
        assertThat(afterIncrement.getCloseFriendsCount()).isEqualTo(1);

        userRepo.decrementFollowersCount(userId);
        userRepo.decrementFollowingCount(userId);
        userRepo.decrementCloseFriendsCount(userId);

        User afterDecrement = userRepo.findById(userId).orElseThrow();
        assertThat(afterDecrement.getFollowersCount()).isEqualTo(0);
        assertThat(afterDecrement.getFollowingsCount()).isEqualTo(0);
        assertThat(afterDecrement.getCloseFriendsCount()).isEqualTo(0);
    }

    @Test
    void findByUserIdIn_shouldReturnOnlyRequestedUsers() {
        User u1 = userRepo.save(buildUser("f1@gmail.com", List.of("Food")));
        User u2 = userRepo.save(buildUser("f2@gmail.com", List.of("History")));
        userRepo.save(buildUser("f3@gmail.com", List.of("Relax")));

        List<User> result = userRepo.findByUserIdIn(List.of(u1.getUserId(), u2.getUserId()));

        assertThat(result).hasSize(2);
        assertThat(result).extracting(User::getUserId)
                .containsExactlyInAnyOrder(u1.getUserId(), u2.getUserId());
    }

    @Test
    void findByUserIdIn_shouldReturnEmpty_whenIdsDoNotExist() {
        List<User> result = userRepo.findByUserIdIn(List.of("missing-1", "missing-2"));
        assertThat(result).isEmpty();
    }

    private User buildUser(String email, List<String> preferences) {
        return User.builder()
                .name(email.split("@")[0])
                .email(email)
                .password("Password@123")
                .role("ROLE_USER")
                .accountType(AccountType.PUBLIC)
                .preferences(new ArrayList<>(preferences))
                .build();
    }
}

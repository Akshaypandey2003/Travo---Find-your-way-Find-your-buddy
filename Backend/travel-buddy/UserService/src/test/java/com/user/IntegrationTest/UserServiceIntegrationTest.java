package com.user.IntegrationTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Map;
import java.util.UUID;
import java.time.Duration;
import java.util.Collections;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.user.Config.JwtProvider;
import com.user.Entity.User;
import com.user.Enum.AccountType;
import com.user.Repository.CloseFriendsRepo;
import com.user.Repository.ConnectionRepo;
import com.user.Repository.FailedNotificationRepo;
import com.user.Repository.PasswordResetTokenRepo;
import com.user.Repository.UserRepo;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
@ActiveProfiles("test")
@EmbeddedKafka(partitions = 1, topics = { "user-events", "notification-events" })
@SuppressWarnings("null")
class UserServiceIntegrationTest {

    @Container
    static MongoDBContainer mongo = new MongoDBContainer("mongo:7.0");

    @SuppressWarnings("resource")
    @Container
    static GenericContainer<?> redis = new GenericContainer<>("redis:7.2-alpine")
            .withExposedPorts(6379);

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongo::getReplicaSetUrl);
        registry.add("spring.data.redis.host", redis::getHost);
        registry.add("spring.data.redis.port", () -> redis.getMappedPort(6379));
    }

    @Autowired
    MockMvc mockMvc;

    @Autowired
    EmbeddedKafkaBroker embeddedKafkaBroker;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    JwtProvider jwtProvider;

    @Autowired
    UserRepo userRepo;

    @Autowired
    ConnectionRepo connectionRepo;

    @Autowired
    CloseFriendsRepo closeFriendsRepo;

    @Autowired
    PasswordResetTokenRepo passwordResetTokenRepo;

    @Autowired
    FailedNotificationRepo failedNotificationRepo;

    @Autowired
    StringRedisTemplate redisTemplate;

    @BeforeEach
    void setup() {
        closeFriendsRepo.deleteAll();
        connectionRepo.deleteAll();
        passwordResetTokenRepo.deleteAll();
        failedNotificationRepo.deleteAll();
        userRepo.deleteAll();
        redisTemplate.getConnectionFactory().getConnection().flushAll();
    }

    @Test
    void authController_register_login_forgot_and_reset_password_flow() throws Exception {
        String email = uniqueEmail("auth");
        String oldPassword = "Password@123";
        String newPassword = "Password@456";

        AuthUser authUser = registerUser("Auth User", email, oldPassword, "male");
        assertThat(authUser.userId()).isNotBlank();
        assertThat(authUser.accessToken()).isNotBlank();

        mockMvc.perform(
                post("/api/v1/auth/login")
                        .header("Authorization", bootstrapAuthHeader())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "email", email,
                                "password", oldPassword))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").isNotEmpty());

        mockMvc.perform(
                post("/api/v1/auth/forgot-password")
                        .header("Authorization", bootstrapAuthHeader())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("email", email))))
                .andExpect(status().isNoContent());

        String resetToken = passwordResetTokenRepo.findAll().get(0).getToken();
        assertThat(resetToken).isNotBlank();

        mockMvc.perform(
                post("/api/v1/auth/reset-password")
                        .header("Authorization", bootstrapAuthHeader())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "token", resetToken,
                                "password", newPassword))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"));

        mockMvc.perform(
                post("/api/v1/auth/login")
                        .header("Authorization", bootstrapAuthHeader())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "email", email,
                                "password", oldPassword))))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(
                post("/api/v1/auth/login")
                        .header("Authorization", bootstrapAuthHeader())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "email", email,
                                "password", newPassword))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").isNotEmpty());
    }

    @Test
    void userController_get_update_delete_and_cache_flow() throws Exception {
        AuthUser user = registerUser("User One", uniqueEmail("user"), "Password@123", "male");

        mockMvc.perform(
                get("/api/v1/users/" + user.userId())
                        .header("Authorization", bearer(user.accessToken())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(user.userId()));

        assertThat(redisTemplate.keys("users::*")).isNotEmpty();

        mockMvc.perform(
                put("/api/v1/users")
                        .with(csrf())
                        .header("Authorization", bearer(user.accessToken()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "name", "Updated Name",
                                "city", "Bangalore"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Name"))
                .andExpect(jsonPath("$.city").value("Bangalore"));

        mockMvc.perform(
                delete("/api/v1/users/" + user.userId())
                        .with(csrf())
                        .header("Authorization", bearer(user.accessToken())))
                .andExpect(status().isNoContent());

        assertThat(userRepo.existsById(user.userId())).isFalse();
    }

    @Test
    void getUserById_shouldFetchFromMongo_andCacheInRedis() throws Exception {
        AuthUser user = registerUser("Cache User", uniqueEmail("cache"), "Password@123", "male");

        mockMvc.perform(
                get("/api/v1/users/" + user.userId())
                        .header("Authorization", bearer(user.accessToken())))
                .andExpect(status().isOk());

        assertThat(redisTemplate.keys("users::*")).isNotEmpty();

        mockMvc.perform(
                get("/api/v1/users/" + user.userId())
                        .header("Authorization", bearer(user.accessToken())))
                .andExpect(status().isOk());
    }

    @Test
    void updateUser_shouldUpdateMongo_publishKafka_evictCache() throws Exception {
        AuthUser user = registerUser("Updater", uniqueEmail("update"), "Password@123", "male");

        mockMvc.perform(
                put("/api/v1/users")
                        .with(csrf())
                        .header("Authorization", bearer(user.accessToken()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "name", "New Name",
                                "city", "Bangalore"))))
                .andExpect(status().isOk());

        User updated = userRepo.findById(user.userId()).orElseThrow();
        assertThat(updated.getName()).isEqualTo("New Name");
        assertThat(updated.getCity()).isEqualTo("Bangalore");

        ConsumerRecords<String, String> records = consumeFromTopic("user-events");
        assertThat(records.count()).isGreaterThan(0);
    }

    @Test
    void deleteUser_shouldDeleteMongo_andPublishKafka() throws Exception {
        AuthUser user = registerUser("Delete User", uniqueEmail("delete"), "Password@123", "male");

        mockMvc.perform(
                delete("/api/v1/users/" + user.userId())
                        .with(csrf())
                        .header("Authorization", bearer(user.accessToken())))
                .andExpect(status().isNoContent());

        assertThat(userRepo.existsById(user.userId())).isFalse();

        ConsumerRecords<String, String> records = consumeFromTopic("user-events");
        assertThat(records.count()).isGreaterThan(0);
    }

    @Test
    void connectionController_follow_and_unfollow_public_user_flow() throws Exception {
        AuthUser follower = registerUser("Follower", uniqueEmail("follower"), "Password@123", "male");
        AuthUser following = registerUser("Following", uniqueEmail("following"), "Password@123", "female");

        mockMvc.perform(
                post("/api/v1/connections/follow/" + following.userId())
                        .with(csrf())
                        .header("Authorization", bearer(follower.accessToken())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.requestFrom").value(follower.userId()))
                .andExpect(jsonPath("$.requestTo").value(following.userId()))
                .andExpect(jsonPath("$.status").value("FOLLOWING"));

        mockMvc.perform(
                get("/api/v1/connections/following")
                        .header("Authorization", bearer(follower.accessToken())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].userId").value(following.userId()));

        mockMvc.perform(
                get("/api/v1/connections/followers")
                        .header("Authorization", bearer(following.accessToken())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].userId").value(follower.userId()));

        mockMvc.perform(
                delete("/api/v1/connections/unfollow/" + following.userId())
                        .with(csrf())
                        .header("Authorization", bearer(follower.accessToken())))
                .andExpect(status().isOk());
    }

    @Test
    void connectionController_private_follow_accept_and_close_friend_flow() throws Exception {
        AuthUser requester = registerUser("Requester", uniqueEmail("requester"), "Password@123", "male");
        AuthUser privateUser = registerUser("Private User", uniqueEmail("private"), "Password@123", "female");

        User target = userRepo.findById(privateUser.userId()).orElseThrow();
        target.setAccountType(AccountType.PRIVATE);
        userRepo.save(target);

        mockMvc.perform(
                post("/api/v1/connections/follow/" + privateUser.userId())
                        .with(csrf())
                        .header("Authorization", bearer(requester.accessToken())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("REQUESTED"));

        mockMvc.perform(
                post("/api/v1/connections/accept/" + requester.userId())
                        .with(csrf())
                        .header("Authorization", bearer(privateUser.accessToken())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("FOLLOWING"));

        mockMvc.perform(
                post("/api/v1/connections/close-friends/" + privateUser.userId())
                        .with(csrf())
                        .header("Authorization", bearer(requester.accessToken())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user.userId").value(privateUser.userId()));

        mockMvc.perform(
                get("/api/v1/connections/close-friends")
                        .header("Authorization", bearer(requester.accessToken())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].user.userId").value(privateUser.userId()));

        mockMvc.perform(
                delete("/api/v1/connections/close-friends/" + privateUser.userId())
                        .with(csrf())
                        .header("Authorization", bearer(requester.accessToken())))
                .andExpect(status().isOk());
    }

    private AuthUser registerUser(String name, String email, String password, String gender) throws Exception {
        MvcResult result = mockMvc.perform(
                post("/api/v1/auth/register")
                        .header("Authorization", bootstrapAuthHeader())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "name", name,
                                "email", email,
                                "gender", gender,
                                "password", password))))
                .andExpect(status().isCreated())
                .andReturn();

        JsonNode node = objectMapper.readTree(result.getResponse().getContentAsString());
        return new AuthUser(
                node.path("user").path("userId").asText(),
                node.path("user").path("email").asText(),
                node.path("accessToken").asText());
    }

    private String bearer(String accessToken) {
        return "Bearer " + accessToken;
    }

    private String bootstrapAuthHeader() {
        String token = jwtProvider.generateToken(
                "integration-bootstrap-user",
                "integration-bootstrap@example.com",
                java.util.List.of("ROLE_USER"));
        return bearer(token);
    }

    private String uniqueEmail(String prefix) {
        return prefix + "_" + UUID.randomUUID() + "@gmail.com";
    }

    private ConsumerRecords<String, String> consumeFromTopic(String topic) {
        Map<String, Object> props = KafkaTestUtils.consumerProps(
                "test-group-" + UUID.randomUUID(),
                "true",
                embeddedKafkaBroker);

        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);

        try (KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props)) {
            consumer.subscribe(Collections.singleton(topic));
            return consumer.poll(Duration.ofSeconds(5));
        }
    }

    private record AuthUser(String userId, String email, String accessToken) {
    }
}

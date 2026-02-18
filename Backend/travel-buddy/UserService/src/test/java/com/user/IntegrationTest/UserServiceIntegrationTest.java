package com.user.IntegrationTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Duration;
import java.util.Collections;
import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.data.redis.core.RedisTemplate;

import org.springframework.http.MediaType;

import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.utils.KafkaTestUtils;

import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import org.springframework.test.web.servlet.MockMvc;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.user.Entity.User;
import com.user.Repository.UserRepo;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")

@EmbeddedKafka(
        partitions = 1,
        topics = {
                "user-events",
                "notification-events"
        })
@SuppressWarnings({"null","deprecation"})
class UserServiceIntegrationTest {

    // ---------------- MongoDB ----------------

    @Container
    static MongoDBContainer mongo =
            new MongoDBContainer("mongo:7.0");

    // ---------------- Redis ----------------

    @SuppressWarnings("resource")
    @Container
    static GenericContainer<?> redis =
            new GenericContainer<>("redis:7.2-alpine")
                    .withExposedPorts(6379);

    // ---------------- Inject Embedded Kafka ----------------

    @Autowired
    EmbeddedKafkaBroker embeddedKafkaBroker;

    // ---------------- Dynamic Properties ----------------

    @DynamicPropertySource
    static void registerProperties(
            DynamicPropertyRegistry registry) {

        registry.add(
                "spring.data.mongodb.uri",
                mongo::getReplicaSetUrl);

        registry.add(
                "spring.data.redis.host",
                redis::getHost);

        registry.add(
                "spring.data.redis.port",
                () -> redis.getMappedPort(6379));
    }

    // ---------------- Beans ----------------

    @Autowired
    MockMvc mockMvc;

    @Autowired
    UserRepo userRepo;

    @Autowired
    RedisTemplate<String, Object> redisTemplate;

    @Autowired
    ObjectMapper objectMapper;

    // ---------------- Setup ----------------

    @BeforeEach
    void setup() {

        userRepo.deleteAll();

        redisTemplate.getConnectionFactory()
                .getConnection()
                .flushAll();
    }

    // ====================================================
    // TEST 1: getUserById → MongoDB + Redis Cache
    // ====================================================

    @Test
    void getUserById_shouldFetchFromMongo_andCacheInRedis()
            throws Exception {

        User user = User.builder()
                .email("test@gmail.com")
                .name("Akshay")
                .password("123")
                .build();

        user = userRepo.save(user);

        // First call → MongoDB
        mockMvc.perform(
                get("/api/v1/users/" + user.getUserId()))
                .andExpect(status().isOk());

        // Verify Redis cache populated
        boolean cacheExists =
                redisTemplate.keys("users::*").size() > 0;

        assertThat(cacheExists).isTrue();

        // Second call → Redis (not MongoDB)
        mockMvc.perform(
                get("/api/v1/users/" + user.getUserId()))
                .andExpect(status().isOk());
    }

    // ====================================================
    // TEST 2: updateUser → MongoDB + Kafka + Redis Evict
    // ====================================================

    @Test
    void updateUser_shouldUpdateMongo_publishKafka_evictCache()
            throws Exception {

        User user = User.builder()
                .email("update@gmail.com")
                .name("Old Name")
                .password("123")
                .build();

        user = userRepo.save(user);
        
        final String userId = user.getUserId();

        String requestBody = """
                {
                  "name": "New Name",
                  "city": "Bangalore"
                }
                """;

        mockMvc.perform(
                put("/api/v1/users")
                        .principal(() -> userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk());

        // Verify Mongo updated
        User updated =
                userRepo.findById(user.getUserId()).orElseThrow();

        assertThat(updated.getName())
                .isEqualTo("New Name");

        // Verify Kafka event published
        ConsumerRecords<String, String> records =
                consumeFromTopic("user-events");

        assertThat(records.count())
                .isGreaterThan(0);
    }

    // ====================================================
    // TEST 3: deleteUser → MongoDB delete + Kafka event
    // ====================================================

    @Test
    void deleteUser_shouldDeleteMongo_andPublishKafka()
            throws Exception {

        User user = User.builder()
                .email("delete@gmail.com")
                .name("Delete Me")
                .password("123")
                .build();

        user = userRepo.save(user);

        mockMvc.perform(
                delete("/api/v1/users/" + user.getUserId()))
                .andExpect(status().isNoContent());

        // Verify MongoDB deletion
        boolean exists =
                userRepo.existsById(user.getUserId());

        assertThat(exists).isFalse();

        // Verify Kafka event published
        ConsumerRecords<String, String> records =
                consumeFromTopic("user-events");

        assertThat(records.count())
                .isGreaterThan(0);
    }

    // ====================================================
    // Kafka Consumer helper
    // ====================================================

    private ConsumerRecords<String, String>
    consumeFromTopic(String topic) {

        Map<String, Object> props =
                KafkaTestUtils.consumerProps(
                        "test-group",
                        "true",
                        embeddedKafkaBroker);

        props.put(
                ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                StringDeserializer.class);

        props.put(
                ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                StringDeserializer.class);

        try (KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props)) {
            consumer.subscribe(Collections.singleton(topic));

            return consumer.poll(Duration.ofSeconds(5));
        }
    }

}

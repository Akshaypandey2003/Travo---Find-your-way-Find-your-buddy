package com.feedback.IntegrationTests;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.feedback.Clients.TripClient;
import com.feedback.DTO.TripSummary;
import com.feedback.Entity.FeedBack;
import com.feedback.Repository.FeedbackRepo;

import static org.mockito.Mockito.*;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc(addFilters = false)
@EmbeddedKafka(
        partitions = 1,
        topics = { "notification-events" },
        bootstrapServersProperty = "spring.kafka.bootstrap-servers"
)
@ActiveProfiles("test")
@SuppressWarnings({"removal"})
class FeedbackIntegrationTest {

    @Container
    static MongoDBContainer mongo = new MongoDBContainer("mongo:7.0");

    @Autowired
    MockMvc mockMvc;

    @Autowired
    FeedbackRepo feedbackRepo;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    TripClient tripClient; // 🔥 External dependency mocked

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongo::getReplicaSetUrl);
    }

    @AfterEach
    void cleanup() {
        feedbackRepo.deleteAll();
    }

    // ---------------- SUBMIT FEEDBACK ----------------

    @Test
    void shouldSubmitFeedback_andProduceKafkaEvent() throws Exception {

        TripSummary tripSummary = new TripSummary();
        tripSummary.setTripId("trip123");
        tripSummary.setTripName("Goa Trip");
        tripSummary.setMembers(Set.of("user2", "user3"));

        when(tripClient.getTripSummaryById("trip123"))
                .thenReturn(tripSummary);

        FeedBack feedback = new FeedBack();
        feedback.setTripId("trip123");
        feedback.setAuthorId("user1");
        feedback.setComment("Amazing trip!");

        mockMvc.perform(post("/feedback/submit")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(feedback)))
                .andExpect(status().isCreated());

        // ---------- Mongo Assert ----------
        assertThat(feedbackRepo.findAll()).hasSize(1);

        // ---------- Kafka Assert ----------
        Map<String, Object> consumerProps = new HashMap<>();
        consumerProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
                System.getProperty("spring.kafka.bootstrap-servers"));
        consumerProps.put(ConsumerConfig.GROUP_ID_CONFIG, "feedback-test-group");
        consumerProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        consumerProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        consumerProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        try (KafkaConsumer<String, String> consumer = new KafkaConsumer<>(consumerProps)) {
            consumer.subscribe(Collections.singleton("notification-events"));
            ConsumerRecords<String, String> records =
                    consumer.poll(Duration.ofSeconds(5));

            assertThat(records.count()).isGreaterThanOrEqualTo(2); // one per member
        }
    }

    // ---------------- DUPLICATE FEEDBACK ----------------

    @Test
void shouldRejectDuplicateFeedback() throws Exception {

    feedbackRepo.save(
            FeedBack.builder()
                    .tripId("trip123")
                    .authorId("user1")
                    .comment("Nice")
                    .build()
    );

    FeedBack duplicate = new FeedBack();
    duplicate.setTripId("trip123");
    duplicate.setAuthorId("user1");
    duplicate.setComment("Again");

    mockMvc.perform(post("/feedback/submit")
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(duplicate)))
            .andExpect(status().isBadRequest())
            .andExpect(result ->
                    assertThat(result.getResponse().getContentAsString())
                            .isEqualTo("Feedback already submitted for this trip by the user.")
            );

    assertThat(feedbackRepo.findAll()).hasSize(1);
}


    // ---------------- GET TRIP FEEDBACK ----------------

    @Test
    void shouldGetFeedbackByTripId() throws Exception {
        feedbackRepo.save(
                FeedBack.builder()
                        .tripId("trip123")
                        .authorId("user1")
                        .comment("Nice trip")
                        .build()
        );

        mockMvc.perform(get("/feedback/get-trip-feedback/{tripId}", "trip123"))
                .andExpect(status().isOk());
    }

    // ---------------- CHECK IF SUBMITTED ----------------

    @Test
    void shouldReturnTrue_ifUserAlreadySubmitted() throws Exception {
        feedbackRepo.save(
                FeedBack.builder()
                        .tripId("trip123")
                        .authorId("user1")
                        .comment("Nice trip")
                        .build()
        );

        mockMvc.perform(get("/feedback/check/{tripId}/{userId}", "trip123", "user1"))
                .andExpect(status().isOk())
                .andExpect(result ->
                        assertThat(result.getResponse().getContentAsString())
                                .isEqualTo("true"));
    }

    @Test
    void shouldReturnFalse_ifUserNotSubmitted() throws Exception {
        mockMvc.perform(get("/feedback/check/{tripId}/{userId}", "trip123", "user99"))
                .andExpect(status().isOk())
                .andExpect(result ->
                        assertThat(result.getResponse().getContentAsString())
                                .isEqualTo("false"));
    }
}

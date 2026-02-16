package com.trip.IntegrationTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Duration;
import java.time.LocalDate;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.StringDeserializer;
import com.trip.Entity.Trip;
import com.trip.Repositories.TripRespository;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc(addFilters = false)
@EmbeddedKafka(partitions = 1, topics = {
        "notification-events" }, bootstrapServersProperty = "spring.kafka.bootstrap-servers")
@ActiveProfiles("test")
class TripIntegrationTest {

    @Container
    static MongoDBContainer mongo = new MongoDBContainer("mongo:7.0");

    @Autowired
    MockMvc mockMvc;

    @Autowired
    TripRespository tripRepo;

    @Autowired
    ObjectMapper objectMapper;

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongo::getReplicaSetUrl);
    }

    @AfterEach
    void cleanup() {
        tripRepo.deleteAll();
    }

    // ---------------- CREATE TRIP ----------------

    @Test
    void shouldCreateTrip() throws Exception {
        Trip trip = new Trip();
        trip.setTripName("Goa Trip");
        trip.setTripCity("Goa");
        trip.setTripStartDate(LocalDate.now().plusDays(5));

        mockMvc.perform(post("/trip/create-trip")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(trip)))
                .andExpect(status().isCreated());

        assertThat(tripRepo.findAll()).hasSize(1);
    }

    // ---------------- UPDATE TRIP ----------------

    @Test
    void shouldUpdateTrip() throws Exception {
        Trip trip = tripRepo.save(Trip.builder().tripName("Manali Trip").tripCity("Manali").build());

        trip.setTripCity("Shimla");

        mockMvc.perform(put("/trip/update-trip")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(trip)))
                .andExpect(status().isOk());

        Trip updated = tripRepo.findById(trip.getTripId()).get();
        assertThat(updated.getTripCity()).isEqualTo("Shimla");
    }

    // ---------------- DELETE TRIP ----------------

    @Test
    void shouldDeleteTrip() throws Exception {
        Trip trip = tripRepo.save(new Trip());
        String id = trip.getTripId();

        mockMvc.perform(delete("/trip/delete-trip/" + id))
                .andExpect(status().isCreated());

        assertThat(tripRepo.findById(id)).isEmpty();
    }

    // ---------------- SEND TRIP REQUEST (Kafka Test) ----------------

    @Test
    void shouldSendTripRequestAndProduceKafkaEvent() throws Exception {

        Trip trip = tripRepo.save(Trip.builder().tripCity("Jaipur").tripName("Explore pink City")
                .tripOwnerId("Ak1").tripOwnerName("Akshay").build());

        String tripId = trip.getTripId();

        mockMvc.perform(post("/trip/send-trip-request/" + tripId + "/user1"))
                .andExpect(status().isOk());

        // ---------- Kafka Assert (same as UserService) ----------
        Map<String, Object> consumerProps = new HashMap<>();
        consumerProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
                System.getProperty("spring.kafka.bootstrap-servers"));
        consumerProps.put(ConsumerConfig.GROUP_ID_CONFIG, "test-group");
        consumerProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        consumerProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        consumerProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        try (KafkaConsumer<String, String> consumer = new KafkaConsumer<>(consumerProps)) {
            consumer.subscribe(Collections.singleton("notification-events"));
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(5));
            assertThat(records.count()).isGreaterThan(0);
        }
    }

    // ---------------- ACCEPT TRIP REQUEST ----------------

    @Test
    void shouldAcceptTripRequestAndProduceKafkaEvent() throws Exception {
        Trip trip = tripRepo.save(Trip.builder().tripCity("Jaipur").tripName("Explore pink City")
                .tripOwnerId("Ak1").tripOwnerName("Akshay").tripRequests(Set.of("user1")).build());

        mockMvc.perform(post("/trip/accept-trip-request/notif1/" +
                trip.getTripId() + "/user1"))
                .andExpect(status().isOk());

        Trip updated = tripRepo.findById(trip.getTripId()).orElseThrow();
        assertThat(updated.getTripMembers()).contains("user1");

        Map<String, Object> consumerProps = new HashMap<>();
        consumerProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
                System.getProperty("spring.kafka.bootstrap-servers"));
        consumerProps.put(ConsumerConfig.GROUP_ID_CONFIG, "test-group");
        consumerProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        consumerProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        consumerProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        try (KafkaConsumer<String, String> consumer = new KafkaConsumer<>(consumerProps)) {
            consumer.subscribe(Collections.singleton("notification-events"));
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(5));
            assertThat(records.count()).isGreaterThan(0);
        }
    }

    // ---------------- GET TRIP ----------------

    @Test
    void shouldGetTripById() throws Exception {
        Trip trip = tripRepo.save(new Trip());

        mockMvc.perform(get("/trip/get-trip/" + trip.getTripId()))
                .andExpect(status().isOk());
    }

    // ---------------- GET ALL TRIPS ----------------

    @Test
    void shouldGetAllTrips() throws Exception {
        tripRepo.save(new Trip());
        tripRepo.save(new Trip());

        mockMvc.perform(get("/trip/get-all-trips"))
                .andExpect(status().isOk());
    }

    // ---------------- REMOVE MEMBER ----------------

    @Test
    void shouldRemoveTripMember() throws Exception {
        Trip trip = new Trip();
        trip.setTripMembers(new java.util.LinkedHashSet<>(List.of("user1")));
        trip = tripRepo.save(trip);

        mockMvc.perform(delete("/trip/remove-trip-member/" +
                trip.getTripId() + "/user1"))
                .andExpect(status().isOk());

        Trip updated = tripRepo.findById(trip.getTripId()).get();
        assertThat(updated.getTripMembers()).doesNotContain("user1");
    }
}

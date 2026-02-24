package com.trip.IntegrationTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.trip.Config.JwtProvider;
import com.trip.Entity.Trip;
import com.trip.Entity.Trip.TripType;
import com.trip.Entity.TripRequest;
import com.trip.Entity.TripRequest.RequestStatus;
import com.trip.Repositories.TripRequestRepository;
import com.trip.Repositories.TripRespository;
import com.trip.Services.TripDomainEventPublisher;
import com.trip.Services.TripNotificationProducer;

@SpringBootTest
@Testcontainers(disabledWithoutDocker = true)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@SuppressWarnings("removal")
class TripIntegrationTest {

    @Container
    static MongoDBContainer mongo = new MongoDBContainer("mongo:7.0");

    @SuppressWarnings("resource")
    @Container
    static GenericContainer<?> redis = new GenericContainer<>("redis:7.2-alpine")
            .withExposedPorts(6379);

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongo::getReplicaSetUrl);
        registry.add("spring.data.redis.host", redis::getHost);
        registry.add("spring.data.redis.port", () -> redis.getMappedPort(6379));
        registry.add("spring.kafka.bootstrap-servers", () -> "localhost:9092");
        registry.add("spring.task.scheduling.enabled", () -> "false");
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TripRespository tripRepo;

    @Autowired
    private TripRequestRepository tripRequestRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtProvider jwtProvider;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @MockBean
    private TripNotificationProducer tripNotificationProducer;

    @MockBean
    private TripDomainEventPublisher tripDomainEventPublisher;

    @BeforeEach
    void setup() {
        redisTemplate.getConnectionFactory().getConnection().flushAll();
    }

    @AfterEach
    void cleanup() {
        tripRequestRepository.deleteAll();
        tripRepo.deleteAll();
        redisTemplate.getConnectionFactory().getConnection().flushAll();
    }

    @Test
    void createTrip_success_persistsTrip() throws Exception {
        Trip trip = buildTrip("owner1");

        mockMvc.perform(post("/api/v1/trips")
                        .header("Authorization", authHeader("owner1"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(trip)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.tripOwnerId").value("owner1"));

        List<Trip> allTrips = tripRepo.findAll();
        assertThat(allTrips).hasSize(1);
        assertThat(allTrips.get(0).getPendingRequestCount()).isZero();
        assertThat(allTrips.get(0).getTotalRequestCount()).isZero();
    }

    @Test
    void createTrip_failure_whenOwnerMismatch() throws Exception {
        Trip trip = buildTrip("other-user");

        mockMvc.perform(post("/api/v1/trips")
                        .header("Authorization", authHeader("owner1"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(trip)))
                .andExpect(status().isForbidden());

        assertThat(tripRepo.count()).isZero();
    }

    @Test
    void sendTripRequest_success_persistsRequestAndCounters() throws Exception {
        Trip trip = tripRepo.save(buildTrip("owner1"));

        mockMvc.perform(post("/api/v1/trips/send-trip-request/{tripId}/{requestFrom}", trip.getTripId(), "userA")
                        .header("Authorization", authHeader("userA")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"));

        Optional<TripRequest> request = tripRequestRepository.findByTripIdAndRequesterUserId(trip.getTripId(), "userA");
        Trip updatedTrip = tripRepo.findById(trip.getTripId()).orElseThrow();

        assertThat(request).isPresent();
        assertThat(request.get().getStatus()).isEqualTo(RequestStatus.PENDING);
        assertThat(updatedTrip.getPendingRequestCount()).isEqualTo(1);
        assertThat(updatedTrip.getTotalRequestCount()).isEqualTo(1);
    }

    @Test
    void sendTripRequest_failure_whenPrincipalMismatch_returns500() throws Exception {
        Trip trip = tripRepo.save(buildTrip("owner1"));

        mockMvc.perform(post("/api/v1/trips/send-trip-request/{tripId}/{requestFrom}", trip.getTripId(), "userA")
                        .header("Authorization", authHeader("otherUser")))
                .andExpect(status().isInternalServerError());

        assertThat(tripRequestRepository.findByTripIdAndRequesterUserId(trip.getTripId(), "userA")).isEmpty();
    }

    @Test
    void acceptTripRequest_success_updatesRequestAndTripMembers() throws Exception {
        Trip trip = tripRepo.save(buildTrip("owner1"));
        tripRequestRepository.save(TripRequest.builder()
                .tripId(trip.getTripId())
                .ownerUserId("owner1")
                .requesterUserId("userA")
                .status(RequestStatus.PENDING)
                .build());

        mockMvc.perform(post("/api/v1/trips/accept-trip-request/n1/{tripId}/{requestFrom}", trip.getTripId(), "userA")
                        .header("Authorization", authHeader("owner1")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"));

        Trip updatedTrip = tripRepo.findById(trip.getTripId()).orElseThrow();
        TripRequest updatedRequest = tripRequestRepository.findByTripIdAndRequesterUserId(trip.getTripId(), "userA").orElseThrow();

        assertThat(updatedTrip.getTripMembers()).contains("userA");
        assertThat(updatedTrip.getPendingRequestCount()).isZero();
        assertThat(updatedRequest.getStatus()).isEqualTo(RequestStatus.ACCEPTED);
    }

    @Test
    void acceptTripRequest_failure_whenNotOwner_returns500() throws Exception {
        Trip trip = tripRepo.save(buildTrip("owner1"));

        mockMvc.perform(post("/api/v1/trips/accept-trip-request/n1/{tripId}/{requestFrom}", trip.getTripId(), "userA")
                        .header("Authorization", authHeader("not-owner")))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void getPendingRequests_forbidden_whenNotOwner() throws Exception {
        Trip trip = tripRepo.save(buildTrip("owner1"));
        tripRequestRepository.save(TripRequest.builder()
                .tripId(trip.getTripId())
                .ownerUserId("owner1")
                .requesterUserId("userA")
                .status(RequestStatus.PENDING)
                .build());

        mockMvc.perform(get("/api/v1/trips/{tripId}/requests/pending", trip.getTripId())
                        .header("Authorization", authHeader("intruder")))
                .andExpect(status().isForbidden());
    }

    @Test
    void rejectTripRequest_success_updatesRequestStatus() throws Exception {
        Trip trip = tripRepo.save(buildTrip("owner1"));
        tripRequestRepository.save(TripRequest.builder()
                .tripId(trip.getTripId())
                .ownerUserId("owner1")
                .requesterUserId("userA")
                .status(RequestStatus.PENDING)
                .build());

        mockMvc.perform(patch("/api/v1/trips/{tripId}/requests/{requesterUserId}/reject", trip.getTripId(), "userA")
                        .header("Authorization", authHeader("owner1")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("REJECTED"));

        TripRequest updatedRequest = tripRequestRepository.findByTripIdAndRequesterUserId(trip.getTripId(), "userA").orElseThrow();
        assertThat(updatedRequest.getStatus()).isEqualTo(RequestStatus.REJECTED);
    }

    @Test
    void cancelTripRequest_success_byRequester_updatesRequestStatus() throws Exception {
        Trip trip = tripRepo.save(buildTrip("owner1"));
        tripRequestRepository.save(TripRequest.builder()
                .tripId(trip.getTripId())
                .ownerUserId("owner1")
                .requesterUserId("userA")
                .status(RequestStatus.PENDING)
                .build());

        mockMvc.perform(patch("/api/v1/trips/{tripId}/requests/{requesterUserId}/cancel", trip.getTripId(), "userA")
                        .header("Authorization", authHeader("userA")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CANCELLED"));

        TripRequest updatedRequest = tripRequestRepository.findByTripIdAndRequesterUserId(trip.getTripId(), "userA").orElseThrow();
        assertThat(updatedRequest.getStatus()).isEqualTo(RequestStatus.CANCELLED);
    }

    @Test
    void deleteTrip_success_removesTripAndTripRequests() throws Exception {
        Trip trip = tripRepo.save(buildTrip("owner1"));
        tripRequestRepository.save(TripRequest.builder()
                .tripId(trip.getTripId())
                .ownerUserId("owner1")
                .requesterUserId("userA")
                .status(RequestStatus.PENDING)
                .build());

        mockMvc.perform(delete("/api/v1/trips/{tripId}", trip.getTripId())
                        .header("Authorization", authHeader("owner1")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$['status: ']").value("trip deleted successfully."));

        assertThat(tripRepo.findById(trip.getTripId())).isEmpty();
        assertThat(tripRequestRepository.findByTripIdAndRequesterUserId(trip.getTripId(), "userA")).isEmpty();
    }

    @Test
    void getTripsByUser_forbidden_whenPrincipalMismatch() throws Exception {
        mockMvc.perform(get("/api/v1/trips/get-trips-by-user/userA")
                        .header("Authorization", authHeader("userB")))
                .andExpect(status().isForbidden());
    }

    private String authHeader(String userId) {
        String token = jwtProvider.generateToken(userId, List.of("ROLE_USER"));
        return "Bearer " + token;
    }

    private Trip buildTrip(String ownerId) {
        return Trip.builder()
                .tripName("Goa Trip")
                .tripOwnerId(ownerId)
                .tripOwnerName("Owner")
                .tripCity("Goa")
                .tripCountry("India")
                .tripStartDate(LocalDate.now().plusDays(5))
                .tripEndDate(LocalDate.now().plusDays(8))
                .tripType(TripType.GROUP)
                .tripMembers(new LinkedHashSet<>(List.of(ownerId)))
                .build();
    }
}

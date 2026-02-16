// package com.user.IntegrationTest;

// import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
// import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// import java.time.Duration;
// import java.util.Collections;
// import java.util.HashMap;
// import java.util.Map;

// import org.apache.kafka.clients.consumer.ConsumerConfig;
// import org.apache.kafka.clients.consumer.ConsumerRecords;
// import org.apache.kafka.clients.consumer.KafkaConsumer;
// import org.apache.kafka.common.serialization.StringDeserializer;
// import org.junit.jupiter.api.Test;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
// import org.springframework.boot.test.context.SpringBootTest;
// import org.springframework.kafka.test.context.EmbeddedKafka;
// import org.springframework.test.context.ActiveProfiles;
// import org.springframework.test.context.DynamicPropertyRegistry;
// import org.springframework.test.context.DynamicPropertySource;
// import org.springframework.test.web.servlet.MockMvc;
// import org.testcontainers.containers.MongoDBContainer;
// import org.testcontainers.junit.jupiter.Container;
// import org.testcontainers.junit.jupiter.Testcontainers;

// import com.user.Config.JwtProvider;
// import com.user.Entity.User;
// import com.user.Repository.ConnectionRepo;
// import com.user.Repository.UserRepo;

// @SpringBootTest
// @Testcontainers
// @AutoConfigureMockMvc(addFilters = false)
// @EmbeddedKafka(
//     partitions = 1,
//     topics = { "notification-events" },
//     bootstrapServersProperty = "spring.kafka.bootstrap-servers"
// )
// @ActiveProfiles("test")
// @SuppressWarnings("unused")
// public class UserServiceIntegrationTest {

//     @Container
//     static MongoDBContainer mongo = new MongoDBContainer("mongo:7.0");

//     @Autowired
//     private MockMvc mockMvc;

//     @Autowired
//     private UserRepo userRepo;

//     @Autowired
//     private ConnectionRepo connectionRepo;

//     // @Autowired
//     // private JwtProvider jwtProvider;

//     // ------------------ Dynamic Properties ------------------
//     @DynamicPropertySource
//     static void setProperties(DynamicPropertyRegistry registry) {
//         registry.add("spring.data.mongodb.uri", mongo::getReplicaSetUrl);
//     }


    
//     // ------------------ Test Method ------------------
//     @Test
//     void sendFriendRequest_shouldSaveToDB_andSendKafkaEvent() throws Exception {

//         // ---------- Arrange: create sender and receiver ----------
//         User sender = new User();
//         sender.setEmail("u1@gmail.com");
//         sender.setPassword("123");
//         sender.setGender("male");

//         User receiver = new User();
//         receiver.setEmail("u2@gmail.com");
//         receiver.setPassword("123");
//         receiver.setGender("female");

//         sender = userRepo.save(sender);
//         receiver = userRepo.save(receiver);

//         // ---------- Act: send friend request ----------
//         mockMvc.perform(post("/connection/send/" + sender.getUserId() + "/" + receiver.getUserId()))
//                 .andExpect(status().isOk());

//         // ---------- Assert: database ----------
//         assert connectionRepo.findAll().size() == 1;

//         User updatedSender = userRepo.findById(sender.getUserId()).get();
//         User updatedReceiver = userRepo.findById(receiver.getUserId()).get();

//         assert updatedSender.getFollowing().contains(receiver.getUserId());
//         assert updatedReceiver.getFollowers().contains(sender.getUserId());

//         // ---------- Assert: Kafka ----------
//         Map<String, Object> consumerProps = new HashMap<>();
//         consumerProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
//                 System.getProperty("spring.kafka.bootstrap-servers")); // EmbeddedKafka broker
//         consumerProps.put(ConsumerConfig.GROUP_ID_CONFIG, "test-group");
//         consumerProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
//         consumerProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
//         consumerProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

//         try (KafkaConsumer<String, String> consumer = new KafkaConsumer<>(consumerProps)) {
//             consumer.subscribe(Collections.singleton("notification-events"));
//             ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(5));
//             assert records.count() > 0;
//         }
//     }
// }

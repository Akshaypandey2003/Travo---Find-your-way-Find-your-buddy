package com.chat.IntegrationTests;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;

import java.time.Duration;
import java.util.*;

import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
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

import com.chat.Entity.Chat;
import com.chat.Entity.Message;
import com.chat.Repository.ChatRepo;
import com.chat.Repository.MessageRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@EnabledIfSystemProperty(named = "runDockerTests", matches = "true")
@Testcontainers
@AutoConfigureMockMvc(addFilters = false)
@EmbeddedKafka(
        partitions = 1,
        topics = { "notification-events" },
        bootstrapServersProperty = "spring.kafka.bootstrap-servers"
)
@ActiveProfiles("test")
@SuppressWarnings({"unused"})
class ChatMessageIntegrationTest {

    @Container
    static MongoDBContainer mongo = new MongoDBContainer("mongo:7.0");

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    ChatRepo chatRepo;

    @Autowired
    MessageRepository messageRepo;

    @DynamicPropertySource
    static void setProps(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongo::getReplicaSetUrl);
    }

    @AfterEach
    void cleanup() {
        messageRepo.deleteAll();
        chatRepo.deleteAll();
    }

    // ---------------- CHAT TESTS ----------------

    @Test
    void shouldCreateChat_andProduceKafkaEvent() throws Exception {

        Chat chat = new Chat();
        chat.setParticipants(Set.of("user1", "user2"));
        chat.setGroupChat(false);

        mockMvc.perform(
                post("/api/v1/chats")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(chat))
        ).andExpect(status().isCreated());

        assertThat(chatRepo.findAll()).hasSize(1);

        assertKafkaEventProduced();
    }

    @Test
    void shouldGetChatByUserId() throws Exception {
        chatRepo.save(
                Chat.builder()
                        .participants(Set.of("user1"))
                        .build()
        );

        mockMvc.perform(
                get("/api/v1/chats/user").with(user("user1"))
        ).andExpect(status().isOk());
    }

    @Test
    void shouldReturn404_whenChatNotFound() throws Exception {
        mockMvc.perform(
                get("/api/v1/chats/user").with(user("invalid"))
        ).andExpect(status().isNotFound());
    }

    @Test
    void shouldUpdateFavorite() throws Exception {
        Chat chat = chatRepo.save(
                Chat.builder()
                        .participants(Set.of("user1"))
                        .favoriteBy(new HashSet<>())
                        .build()
        );

        mockMvc.perform(
                put("/api/v1/chats/{chatId}/favorite", chat.getChatId())
                        .with(user("user1"))
        ).andExpect(status().isOk());

        Chat updated = chatRepo.findById(chat.getChatId()).get();
        assertThat(updated.getFavoriteBy()).contains("user1");
    }

    @Test
    void shouldDeleteChat_andMessages() throws Exception {
        Chat chat = chatRepo.save(
                Chat.builder()
                        .participants(Set.of("user1"))
                        .groupName("Test Group")
                        .build()
        );

        mockMvc.perform(
                delete("/api/v1/chats/{chatId}", chat.getChatId())
                        .with(user("user1"))
        ).andExpect(status().isNoContent());

        assertThat(chatRepo.findAll()).isEmpty();
    }

    // ---------------- MESSAGE TESTS ----------------

    @Test
    void shouldSendMessage_andProduceKafkaEvent() throws Exception {

        Chat chat = chatRepo.save(
                Chat.builder()
                        .participants(Set.of("user1", "user2"))
                        .build()
        );

        Message msg = new Message();
        msg.setChatId(chat.getChatId());
        msg.setSenderId("user1");
        msg.setMessageContent("Hello");

        mockMvc.perform(
                post("/api/v1/chats/{chatId}/messages", chat.getChatId())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(msg))
        ).andExpect(status().isCreated());

        assertThat(messageRepo.findAll()).hasSize(1);

        assertKafkaEventProduced();
    }

    @Test
    void shouldGetMessages() throws Exception {
        Chat chat = chatRepo.save(
                Chat.builder()
                        .participants(Set.of("user1"))
                        .build()
        );

        messageRepo.save(
                Message.builder()
                        .chatId(chat.getChatId())
                        .senderId("user1")
                        .messageContent("Hi")
                        .build()
        );

        mockMvc.perform(
                get("/api/v1/chats/{chatId}/messages", chat.getChatId())
        ).andExpect(status().isOk());
    }

    @Test
    void shouldUpdateReadStatus() throws Exception {
        Message msg = messageRepo.save(
                Message.builder()
                        .chatId("chat1")
                        .senderId("user1")
                        .read(false)
                        .build()
        );

        mockMvc.perform(
                put("/api/v1/chats/{chatId}/messages/{messageId}/read",
                        msg.getChatId(), msg.getMessageId())
        ).andExpect(status().isOk());

        assertThat(
                messageRepo.findById(msg.getMessageId()).get().isRead()
        ).isTrue();
    }

    @Test
    void shouldDeleteMessage() throws Exception {
        Message msg = messageRepo.save(
                Message.builder()
                        .chatId("chat1")
                        .senderId("user1")
                        .build()
        );

        mockMvc.perform(
                delete("/api/v1/chats/{chatId}/messages/{messageId}",
                        msg.getChatId(), msg.getMessageId())
        ).andExpect(status().isOk());

        assertThat(messageRepo.findAll()).isEmpty();
    }

    // ---------------- KAFKA ASSERT ----------------

    private void assertKafkaEventProduced() {

        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
                System.getProperty("spring.kafka.bootstrap-servers"));
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "chat-test-group");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        try (KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props)) {
            consumer.subscribe(Collections.singleton("notification-events"));
            ConsumerRecords<String, String> records =
                    consumer.poll(Duration.ofSeconds(5));

            assertThat(records.count()).isGreaterThan(0);
        }
    }
}

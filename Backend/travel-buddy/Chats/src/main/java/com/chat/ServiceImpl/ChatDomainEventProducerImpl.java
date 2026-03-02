package com.chat.ServiceImpl;

import java.time.Instant;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import com.chat.Entity.Chat;
import com.chat.Entity.Message;
import com.chat.Events.ChatDomainEvent;
import com.chat.Service.ChatDomainEventProducer;
import com.events.Notification.FailedNotification;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;

@Service
public class ChatDomainEventProducerImpl implements ChatDomainEventProducer {

    private static final Logger log = LoggerFactory.getLogger(ChatDomainEventProducerImpl.class);
    private static final String TOPIC = "chat-events";

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final MongoTemplate mongoTemplate;

    public ChatDomainEventProducerImpl(
            KafkaTemplate<String, Object> kafkaTemplate,
            MongoTemplate mongoTemplate) {
        this.kafkaTemplate = kafkaTemplate;
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    @CircuitBreaker(name = "chatDomainEventCircuitBreaker", fallbackMethod = "publishChatCreatedFallback")
    @Retry(name = "chatDomainEventRetry")
    public void publishChatCreated(Chat chat) {
        ChatDomainEvent event = ChatDomainEvent.builder()
                .eventType("CHAT_CREATED")
                .aggregateType("CHAT")
                .aggregateId(chat.getChatId())
                .actorUserId(firstAdmin(chat.getGroupAdmin()))
                .payload(Map.of(
                        "chatId", chat.getChatId(),
                        "isGroupChat", chat.isGroupChat(),
                        "groupName", valueOrEmpty(chat.getGroupName()),
                        "participants", chat.getParticipants() == null ? Set.of() : chat.getParticipants()))
                .timestamp(System.currentTimeMillis())
                .build();
        publish(chat.getChatId(), event);
    }

    @Override
    @CircuitBreaker(name = "chatDomainEventCircuitBreaker", fallbackMethod = "publishChatUpdatedFallback")
    @Retry(name = "chatDomainEventRetry")
    public void publishChatUpdated(Chat chat, Map<String, Object> updatedFields) {
        ChatDomainEvent event = ChatDomainEvent.builder()
                .eventType("CHAT_UPDATED")
                .aggregateType("CHAT")
                .aggregateId(chat.getChatId())
                .actorUserId(firstAdmin(chat.getGroupAdmin()))
                .payload(Map.of(
                        "chatId", chat.getChatId(),
                        "updatedFields", updatedFields))
                .timestamp(System.currentTimeMillis())
                .build();
        publish(chat.getChatId(), event);
    }

    @Override
    @CircuitBreaker(name = "chatDomainEventCircuitBreaker", fallbackMethod = "publishChatDeletedFallback")
    @Retry(name = "chatDomainEventRetry")
    public void publishChatDeleted(Chat chat) {
        ChatDomainEvent event = ChatDomainEvent.builder()
                .eventType("CHAT_DELETED")
                .aggregateType("CHAT")
                .aggregateId(chat.getChatId())
                .actorUserId(firstAdmin(chat.getGroupAdmin()))
                .payload(Map.of(
                        "chatId", chat.getChatId(),
                        "isGroupChat", chat.isGroupChat(),
                        "groupName", valueOrEmpty(chat.getGroupName()),
                        "participants", chat.getParticipants() == null ? Set.of() : chat.getParticipants()))
                .timestamp(System.currentTimeMillis())
                .build();
        publish(chat.getChatId(), event);
    }

    @Override
    @CircuitBreaker(name = "chatDomainEventCircuitBreaker", fallbackMethod = "publishMessageSentFallback")
    @Retry(name = "chatDomainEventRetry")
    public void publishMessageSent(Message message, Chat chat) {
        ChatDomainEvent event = ChatDomainEvent.builder()
                .eventType("MESSAGE_SENT")
                .aggregateType("MESSAGE")
                .aggregateId(message.getMessageId())
                .actorUserId(message.getSenderId())
                .payload(Map.of(
                        "messageId", valueOrEmpty(message.getMessageId()),
                        "chatId", valueOrEmpty(message.getChatId()),
                        "messageType", valueOrEmpty(message.getMessageType()),
                        "isGroupChat", chat != null && chat.isGroupChat()))
                .timestamp(System.currentTimeMillis())
                .build();
        publish(message.getChatId(), event);
    }

    @Override
    @CircuitBreaker(name = "chatDomainEventCircuitBreaker", fallbackMethod = "publishMessageUpdatedFallback")
    @Retry(name = "chatDomainEventRetry")
    public void publishMessageUpdated(Message message) {
        ChatDomainEvent event = ChatDomainEvent.builder()
                .eventType("MESSAGE_UPDATED")
                .aggregateType("MESSAGE")
                .aggregateId(message.getMessageId())
                .actorUserId(message.getSenderId())
                .payload(Map.of(
                        "messageId", valueOrEmpty(message.getMessageId()),
                        "chatId", valueOrEmpty(message.getChatId()),
                        "messageType", valueOrEmpty(message.getMessageType())))
                .timestamp(System.currentTimeMillis())
                .build();
        publish(message.getChatId(), event);
    }

    @Override
    @CircuitBreaker(name = "chatDomainEventCircuitBreaker", fallbackMethod = "publishMessageReadFallback")
    @Retry(name = "chatDomainEventRetry")
    public void publishMessageRead(Message message) {
        ChatDomainEvent event = ChatDomainEvent.builder()
                .eventType("MESSAGE_READ")
                .aggregateType("MESSAGE")
                .aggregateId(message.getMessageId())
                .actorUserId(message.getSenderId())
                .payload(Map.of(
                        "messageId", valueOrEmpty(message.getMessageId()),
                        "chatId", valueOrEmpty(message.getChatId()),
                        "isRead", message.isRead()))
                .timestamp(System.currentTimeMillis())
                .build();
        publish(message.getChatId(), event);
    }

    @Override
    @CircuitBreaker(name = "chatDomainEventCircuitBreaker", fallbackMethod = "publishMessageDeletedFallback")
    @Retry(name = "chatDomainEventRetry")
    public void publishMessageDeleted(Message message) {
        ChatDomainEvent event = ChatDomainEvent.builder()
                .eventType("MESSAGE_DELETED")
                .aggregateType("MESSAGE")
                .aggregateId(message.getMessageId())
                .actorUserId(message.getSenderId())
                .payload(Map.of(
                        "messageId", valueOrEmpty(message.getMessageId()),
                        "chatId", valueOrEmpty(message.getChatId())))
                .timestamp(System.currentTimeMillis())
                .build();
        publish(message.getChatId(), event);
    }

    public void publishChatCreatedFallback(Chat chat, Exception ex) {
        saveFailedEvent(chat.getChatId(), ChatDomainEvent.builder()
                .eventType("CHAT_CREATED")
                .aggregateType("CHAT")
                .aggregateId(chat.getChatId())
                .actorUserId(firstAdmin(chat.getGroupAdmin()))
                .payload(Map.of("chatId", chat.getChatId()))
                .timestamp(System.currentTimeMillis())
                .build(), ex);
    }

    public void publishChatUpdatedFallback(Chat chat, Map<String, Object> updatedFields, Exception ex) {
        saveFailedEvent(chat.getChatId(), ChatDomainEvent.builder()
                .eventType("CHAT_UPDATED")
                .aggregateType("CHAT")
                .aggregateId(chat.getChatId())
                .actorUserId(firstAdmin(chat.getGroupAdmin()))
                .payload(Map.of("chatId", chat.getChatId(), "updatedFields", updatedFields))
                .timestamp(System.currentTimeMillis())
                .build(), ex);
    }

    public void publishChatDeletedFallback(Chat chat, Exception ex) {
        saveFailedEvent(chat.getChatId(), ChatDomainEvent.builder()
                .eventType("CHAT_DELETED")
                .aggregateType("CHAT")
                .aggregateId(chat.getChatId())
                .actorUserId(firstAdmin(chat.getGroupAdmin()))
                .payload(Map.of("chatId", chat.getChatId()))
                .timestamp(System.currentTimeMillis())
                .build(), ex);
    }

    public void publishMessageSentFallback(Message message, Chat chat, Exception ex) {
        saveFailedEvent(message.getChatId(), ChatDomainEvent.builder()
                .eventType("MESSAGE_SENT")
                .aggregateType("MESSAGE")
                .aggregateId(message.getMessageId())
                .actorUserId(message.getSenderId())
                .payload(Map.of("messageId", valueOrEmpty(message.getMessageId()), "chatId", valueOrEmpty(message.getChatId())))
                .timestamp(System.currentTimeMillis())
                .build(), ex);
    }

    public void publishMessageUpdatedFallback(Message message, Exception ex) {
        saveFailedEvent(message.getChatId(), ChatDomainEvent.builder()
                .eventType("MESSAGE_UPDATED")
                .aggregateType("MESSAGE")
                .aggregateId(message.getMessageId())
                .actorUserId(message.getSenderId())
                .payload(Map.of("messageId", valueOrEmpty(message.getMessageId()), "chatId", valueOrEmpty(message.getChatId())))
                .timestamp(System.currentTimeMillis())
                .build(), ex);
    }

    public void publishMessageReadFallback(Message message, Exception ex) {
        saveFailedEvent(message.getChatId(), ChatDomainEvent.builder()
                .eventType("MESSAGE_READ")
                .aggregateType("MESSAGE")
                .aggregateId(message.getMessageId())
                .actorUserId(message.getSenderId())
                .payload(Map.of("messageId", valueOrEmpty(message.getMessageId()), "chatId", valueOrEmpty(message.getChatId())))
                .timestamp(System.currentTimeMillis())
                .build(), ex);
    }

    public void publishMessageDeletedFallback(Message message, Exception ex) {
        saveFailedEvent(message.getChatId(), ChatDomainEvent.builder()
                .eventType("MESSAGE_DELETED")
                .aggregateType("MESSAGE")
                .aggregateId(message.getMessageId())
                .actorUserId(message.getSenderId())
                .payload(Map.of("messageId", valueOrEmpty(message.getMessageId()), "chatId", valueOrEmpty(message.getChatId())))
                .timestamp(System.currentTimeMillis())
                .build(), ex);
    }

    private void publish(String key, ChatDomainEvent event) {
        try {
            kafkaTemplate.send(TOPIC, key, event).get();
        } catch (Exception ex) {
            throw new RuntimeException("Failed to publish chat domain event", ex);
        }
    }

    private void saveFailedEvent(String key, ChatDomainEvent event, Exception ex) {
        log.error("Failed to publish chat domain event. eventType={}, aggregateId={}", event.getEventType(), event.getAggregateId(), ex);
        FailedNotification failed = FailedNotification.builder()
                .topic(TOPIC)
                .key(key)
                .event(event)
                .retryCount(0)
                .createdAt(Instant.now())
                .lastRetryAt(null)
                .failureReason(ex.getMessage())
                .build();
        mongoTemplate.save(failed);
    }

    private String firstAdmin(Set<String> admins) {
        if (admins == null || admins.isEmpty()) {
            return "SYSTEM";
        }
        return admins.iterator().next();
    }

    private String valueOrEmpty(String value) {
        return value == null ? "" : value;
    }
}

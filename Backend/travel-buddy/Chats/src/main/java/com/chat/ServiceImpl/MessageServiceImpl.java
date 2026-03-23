package com.chat.ServiceImpl;

import java.time.Instant;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.chat.Entity.Chat;
import com.chat.Entity.ChatParticipant;
import com.chat.Entity.Message;
import com.chat.Exceptions.MessageNotFoundException;
import com.chat.Repository.ChatRepo;
import com.chat.Repository.MessageRepository;
import com.chat.Service.ChatDomainEventProducer;
import com.chat.Service.MessageService;
import com.chat.Service.NotificationProducer;

@Service
@Transactional
public class MessageServiceImpl implements MessageService {

    private static final Logger log = LoggerFactory.getLogger(MessageServiceImpl.class);
    private static final String CACHE_MESSAGES_BY_CHAT_ID = "messagesByChatId";

    private final MessageRepository messageRepo;
    private final ChatRepo chatRepo;
    private final NotificationProducer chatNotificationProducer;
    private final ChatDomainEventProducer chatDomainEventProducer;
    private final CacheManager cacheManager;

    public MessageServiceImpl(
            MessageRepository messageRepo,
            ChatRepo chatRepo,
            NotificationProducer chatNotificationProducer,
            ChatDomainEventProducer chatDomainEventProducer,
            CacheManager cacheManager) {
        this.messageRepo = messageRepo;
        this.chatRepo = chatRepo;
        this.chatNotificationProducer = chatNotificationProducer;
        this.chatDomainEventProducer = chatDomainEventProducer;
        this.cacheManager = cacheManager;
    }

    @Override
    public Message sendMessage(String chatId, Message message) {
        if (message.getChatId() == null || message.getChatId().isBlank()) {
            message.setChatId(chatId);
        } else if (!chatId.equals(message.getChatId())) {
            throw new IllegalArgumentException("Path chatId and payload chatId must match");
        }

        Chat chat = chatRepo.findById(message.getChatId())
                .orElseThrow(() ->
                        new MessageNotFoundException("Chat not found with id: " + message.getChatId())
                );

        // Update recent activity
        chat.setRecentConversationAt(Instant.now());
        chatRepo.save(chat);

        // Persist message
        Message savedMessage = messageRepo.save(message);

        // Notify participants (non-blocking)
        notifyParticipants(chat, savedMessage);
        chatDomainEventProducer.publishMessageSent(savedMessage, chat);
        evictMessagesCache(chatId);

        log.info("Message sent successfully. chatId={}, messageId={}",
                savedMessage.getChatId(), savedMessage.getMessageId());

        return savedMessage;
    }

    private void notifyParticipants(Chat chat, Message savedMessage) {
        for (ChatParticipant participant : chat.getParticipants()) {
            if (!participant.getUserId().equals(savedMessage.getSenderId())) {
                try {
                    chatNotificationProducer.messageSent(
                            savedMessage.getSenderId(),
                            participant.getUserId(),
                            savedMessage.getChatId(),
                            chat.getGroupName()
                    );
                } catch (Exception ex) {
                    // Kafka failure should NOT break message sending
                    log.error(
                            "Failed to send notification. chatId={}, receiverId={}",
                            savedMessage.getChatId(),
                            participant,
                            ex
                    );
                }
            }
        }
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = CACHE_MESSAGES_BY_CHAT_ID, key = "#chatId")
    public List<Message> getMessagesByChatId(String chatId) {

        List<Message> messages = messageRepo.findByChatIdOrderByCreatedAtDesc(chatId);

        if (messages == null || messages.isEmpty()) {
            throw new MessageNotFoundException(
                    "Messages not found for chatId: " + chatId
            );
        }

        return messages;
    }

    @Override
    public void deleteMessage(String messageId) {
        Message existingMessage = messageRepo.findById(messageId)
                .orElseThrow(() ->
                        new MessageNotFoundException("Message not found with id: " + messageId)
                );

        messageRepo.deleteById(messageId);
        chatDomainEventProducer.publishMessageDeleted(existingMessage);
        evictMessagesCache(existingMessage.getChatId());
        log.info("Message deleted successfully. messageId={}", messageId);
    }

    @Override
    public Message updateMessage(String messageId, Message message) {

        Message existingMessage = messageRepo.findById(messageId)
                .orElseThrow(() ->
                        new MessageNotFoundException("Message not found with id: " + messageId)
                );

        if (message.getMessageContent() != null) {
            existingMessage.setMessageContent(message.getMessageContent());
        }

        if (message.getMediaUrl() != null) {
            existingMessage.setMediaUrl(message.getMediaUrl());
        }

        if (message.getMessageType() != null) {
            existingMessage.setMediaType(message.getMediaType());
        }

        existingMessage.setRead(message.isRead());

        Message updatedMessage = messageRepo.save(existingMessage);
        chatDomainEventProducer.publishMessageUpdated(updatedMessage);
        evictMessagesCache(updatedMessage.getChatId());
        return updatedMessage;
    }

    @Override
    public Message updateReadStatus(String messageId) {

        Message existingMessage = messageRepo.findById(messageId)
                .orElseThrow(() ->
                        new MessageNotFoundException("Message not found with id: " + messageId)
                );

        existingMessage.setRead(true);
        Message updatedMessage = messageRepo.save(existingMessage);
        chatDomainEventProducer.publishMessageRead(updatedMessage);
        evictMessagesCache(updatedMessage.getChatId());
        return updatedMessage;
    }

    private void evictMessagesCache(String chatId) {
        Cache cache = cacheManager.getCache(CACHE_MESSAGES_BY_CHAT_ID);
        if (cache != null && chatId != null) {
            cache.evict(chatId);
        }
    }
}

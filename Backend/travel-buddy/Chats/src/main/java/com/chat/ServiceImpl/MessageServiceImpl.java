package com.chat.ServiceImpl;

import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.chat.Entity.Chat;
import com.chat.Entity.Message;
import com.chat.Exceptions.MessageNotFoundException;
import com.chat.Repository.ChatRepo;
import com.chat.Repository.MessageRepository;
import com.chat.Service.MessageService;

@Service
@Transactional
public class MessageServiceImpl implements MessageService {

    private static final Logger log = LoggerFactory.getLogger(MessageServiceImpl.class);

    private final MessageRepository messageRepo;
    private final ChatRepo chatRepo;
    private final ChatNotificationProducer chatNotificationProducer;

    public MessageServiceImpl(
            MessageRepository messageRepo,
            ChatRepo chatRepo,
            ChatNotificationProducer chatNotificationProducer) {
        this.messageRepo = messageRepo;
        this.chatRepo = chatRepo;
        this.chatNotificationProducer = chatNotificationProducer;
    }

    @Override
    public Message sendMessage(Message message) {

        Chat chat = chatRepo.findById(message.getChatId())
                .orElseThrow(() ->
                        new MessageNotFoundException("Chat not found with id: " + message.getChatId())
                );

        // Update recent activity
        chat.setRecentConversationAt(LocalDateTime.now());
        chatRepo.save(chat);

        // Persist message
        Message savedMessage = messageRepo.save(message);

        // Notify participants (non-blocking)
        notifyParticipants(chat, savedMessage);

        log.info("Message sent successfully. chatId={}, messageId={}",
                savedMessage.getChatId(), savedMessage.getMessageId());

        return savedMessage;
    }

    private void notifyParticipants(Chat chat, Message savedMessage) {
        for (String participant : chat.getParticipants()) {
            if (!participant.equals(savedMessage.getSenderId())) {
                try {
                    chatNotificationProducer.messageSent(
                            savedMessage.getSenderId(),
                            participant,
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
    public List<Message> getMessage(String chatId) {

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
        if (!messageRepo.existsById(messageId)) {
            throw new MessageNotFoundException(
                    "Message not found with id: " + messageId
            );
        }

        messageRepo.deleteById(messageId);
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

        return messageRepo.save(existingMessage);
    }

    @Override
    public Message updateReadStatus(String messageId) {

        Message existingMessage = messageRepo.findById(messageId)
                .orElseThrow(() ->
                        new MessageNotFoundException("Message not found with id: " + messageId)
                );

        existingMessage.setRead(true);
        return messageRepo.save(existingMessage);
    }
}

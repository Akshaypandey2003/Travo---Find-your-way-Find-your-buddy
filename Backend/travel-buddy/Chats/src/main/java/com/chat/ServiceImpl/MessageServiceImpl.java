package com.chat.ServiceImpl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.chat.Entity.Chat;
import com.chat.Entity.Message;
import com.chat.Exceptions.MessageNotFoundException;
import com.chat.Repository.ChatRepo;
import com.chat.Repository.MessageRepository;
import com.chat.Service.MessageService;
import com.events.Entity.NotificationEvent;

@Service
@SuppressWarnings("unused")
public class MessageServiceImpl implements MessageService {

    @Autowired
    private MessageRepository messageRepo;

    @Autowired
    private ChatRepo chatRepo;

    @Autowired
    private KafkaTemplate<String, NotificationEvent> kafkaTemplate;

    @Autowired
    private ChatNotificationProducer chatNotificationProducer;


    @Override
    public Message sendMessage(Message message) {
        try {

            Chat chat = chatRepo.findById(message.getChatId())
                    .orElseThrow(() -> new RuntimeException("Chat not found"));
            chat.setRecentConversationAt(LocalDateTime.now());

            chatRepo.save(chat);
            
            Message savedMessage = messageRepo.save(message);

            for(String participant: chat.getParticipants()) {
                if(!participant.equals(savedMessage.getSenderId())) {
                    chatNotificationProducer.messageSent(
                        savedMessage.getSenderId(),
                        participant,
                        message.getChatId(),
                        chat.getGroupName()
                    );
                }
            }

            return savedMessage;
        } catch (Exception e) {
            throw new RuntimeException("Error sending message: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Message> getMessage(String chatId) {
        try {
            List<Message> messages = messageRepo.findByChatIdOrderByCreatedAtDesc(chatId);

            if (messages == null || messages.isEmpty()) {
                throw new MessageNotFoundException("Messages not found for chatId: " + chatId);
            }
            return messages;
        } catch (Exception e) {
            throw new RuntimeException("Error sending message: " + e.getMessage(), e);
        }
    }

    @Override
    public void deleteMessage(String messageId) {
       try {
        messageRepo.deleteById(messageId);
       } catch (Exception e) {
          throw new RuntimeException("Error sending message: " + e.getMessage(), e);
       }
    }

    @Override
    public Message updateMessage(String messageId, Message message) 
    {
        Message existingMessage = messageRepo.findById(messageId)
                .orElseThrow(() -> new MessageNotFoundException("Message not found with id: " + messageId));
        
        if(message.getMessageContent()!=null)
        existingMessage.setMessageContent(message.getMessageContent());

        if(message.getMediaUrl()!=null)
        existingMessage.setMediaUrl(message.getMediaUrl());
        if(message.getMessageType()!=null)
        existingMessage.setMediaType(message.getMediaType());
        existingMessage.setRead(message.isRead());

        return messageRepo.save(existingMessage);
    }
    @Override
    public Message updateReadStatus(String messageId) 
    {
        Message existingMessage = messageRepo.findById(messageId)
                .orElseThrow(() -> new MessageNotFoundException("Message not found with id: " + messageId));
        
        
        existingMessage.setRead(true);

        return messageRepo.save(existingMessage);
    }

}

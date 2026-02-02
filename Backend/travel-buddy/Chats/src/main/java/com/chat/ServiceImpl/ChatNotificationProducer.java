package com.chat.ServiceImpl;

import java.util.Map;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.chat.Service.NotificationProducer;
import com.events.Entity.NotificationEvent;

@Service
public class ChatNotificationProducer implements NotificationProducer {

    private final KafkaTemplate<String, NotificationEvent> kafkaTemplate;

    public ChatNotificationProducer(KafkaTemplate<String, NotificationEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void messageSent(String senderId, String receiverId, String chatId, String chatName) {

        String message = chatName != null && !chatName.isEmpty() ? 
            "sent you a message in " + chatName : 
            "sent you a message";

        NotificationEvent event = new NotificationEvent(
            "MESSAGE_SENT",
            senderId,
            receiverId,
            message,
            "CHAT",
            chatId,
            Map.of("chatId", chatId,"chatName", chatName),
            System.currentTimeMillis()
        );

        kafkaTemplate.send("notification-events", chatId, event);
    }

    public void groupCreated(String senderId, String receiverId, String chatId, String chatName) {

        NotificationEvent event = new NotificationEvent(
            "GROUP_CREATED",
            senderId,
            receiverId,
            "added you to " + chatName,
            "CHAT",
            chatId,
            Map.of("chatId", chatId, "chatName", chatName),
            System.currentTimeMillis()
        );

        kafkaTemplate.send("notification-events", chatId, event);
    }

    public void groupUpdated(String senderId, String receiverId, String chatId, String chatName) {

        NotificationEvent event = new NotificationEvent(
            "GROUP_UPDATED",
            senderId,
            receiverId,
            "has updated the group " + chatName,
            "CHAT",
            chatId,
            Map.of("chatId", chatId, "chatName", chatName),
            System.currentTimeMillis()
        );

        kafkaTemplate.send("notification-events", senderId, event);
    }

    public void addGroupMember(String senderId, String receiverId, String chatId, String chatName) {

        NotificationEvent event = new NotificationEvent(
            "GROUP_MEMBER_ADDED",
            senderId,
            receiverId,
            "has added you to the group " + chatName,
            "CHAT",
            chatId,
            Map.of("chatId", chatId, "chatName", chatName),
            System.currentTimeMillis()
        );

        kafkaTemplate.send("notification-events", senderId, event);
    }
    public void removeGroupMember(String senderId, String receiverId, String chatId,String chatName) {

        NotificationEvent event = new NotificationEvent(
            "GROUP_MEMBER_REMOVED",
            senderId,
            receiverId,
            "has removed you from the group " + chatName,
            "CHAT",
            chatId,
            Map.of("chatId", chatId),
            System.currentTimeMillis()
        );

        kafkaTemplate.send("notification-events", senderId, event);
    }

    public void deleteGroup(String senderId, String receiverId, String chatId, String chatName) {

        NotificationEvent event = new NotificationEvent(
            "GROUP_DELETED",
            senderId,
            receiverId,
            "has deleted the group " + chatName,
            "CHAT",
            chatId,
            Map.of("chatId", chatId, "chatName", chatName),
            System.currentTimeMillis()
        );

        kafkaTemplate.send("notification-events", senderId, event);
    }
}

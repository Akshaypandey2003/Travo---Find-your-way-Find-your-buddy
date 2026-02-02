package com.notification.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.events.Entity.NotificationEvent;
import com.notification.Clients.UserServiceClient;
import com.notification.Controller.NotificationSocketController;
import com.notification.DTO.NotificationMapper;
import com.notification.DTO.UserSummary;
import com.notification.Entity.Notification;

@Service
@SuppressWarnings("unused")
public class NotificationConsumer {
    

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private UserServiceClient userServiceClient;


    private final NotificationSocketController socketController;

    public NotificationConsumer(NotificationSocketController socketController) {
        this.socketController = socketController;
    }

    @KafkaListener(topics = "notification-events")
    public void consume(NotificationEvent event) {


        System.out.println("Received Notification Event: " + event);
        
         // ⚠️ fetch SENDER details, not receiver
        UserSummary sender =
                userServiceClient.getUserSummaryById(event.getSenderId());


        Notification notification =
                NotificationMapper.toEntity(event, sender, event.getReceiverId());

        notificationService.saveNotification(notification);

        socketController.sendNotification(event.getReceiverId(), notification);
    }
}

package com.user.ServiceImpl;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.events.Entity.NotificationEvent;

@Service
public class UserNotificationProducer {
    
    private final KafkaTemplate<String, NotificationEvent> kafkaTemplate;

    public UserNotificationProducer(KafkaTemplate<String, NotificationEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void friendRequestSend(String senderId, String receiverId) {

        NotificationEvent event = new NotificationEvent(
            "FRIEND_REQUEST_SENT",
            senderId,
            receiverId,
            "has sent you a friend request.",
            "CONNECTION",
            null,
            null,
            System.currentTimeMillis()
        );

      kafkaTemplate.send("notification-events", receiverId, event)
    .whenComplete((result, ex) -> {
        if (ex == null) {
            System.out.println(
                "Kafka send success. Topic=" +
                result.getRecordMetadata().topic() +
                ", Partition=" +
                result.getRecordMetadata().partition() +
                ", Offset=" +
                result.getRecordMetadata().offset()
            );
        } else {
            System.out.println("Kafka send failed: " + ex.getMessage());
        }
    });
    }


    public void friendRequestAccept(String senderId, String receiverId) {

        NotificationEvent event = new NotificationEvent(
            "FRIEND_REQUEST_ACCEPTED",
            senderId,
            receiverId,
            "has accepted your friend request.",
            "CONNECTION",
            null,
            null,
            System.currentTimeMillis()
        );

        kafkaTemplate.send("notification-events", receiverId, event);
    }
}

package com.feedback.ServiceImpl;

import java.util.Map;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.events.Entity.NotificationEvent;
import com.feedback.Service.NotificationProducer;

@Service
public class NotificationProducerImpl implements NotificationProducer {

    private final KafkaTemplate<String, NotificationEvent> kafkaTemplate;

    public NotificationProducerImpl(KafkaTemplate<String, NotificationEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void sendFeedbackNotification(String senderId, String receiverId, String tripId,String tripName) {

        NotificationEvent event = new NotificationEvent(
            "FEEDBACK_SUBMITTED",
            senderId,
            receiverId,
            "has submitted feedback for " + tripName ,
            "FEEDBACK",
            tripId,
            Map.of("tripId", tripId, "tripName", tripName),
            System.currentTimeMillis()
        );

      kafkaTemplate.send("notification-events", receiverId, event);
    }
    
}

package com.trip.ServiceImpl;

import java.util.Map;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.events.Entity.NotificationEvent;
import com.trip.Services.TripNotificationProducer;

@Service
public class TripNotificationProducerImpl implements TripNotificationProducer {

     private final KafkaTemplate<String, NotificationEvent> kafkaTemplate;

    public TripNotificationProducerImpl(KafkaTemplate<String, NotificationEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void sendTripRequestNotification(String senderId, String receiverId, String tripId, String tripName) {

         NotificationEvent event = new NotificationEvent(
            "TRIP_REQUEST_SENT",
            senderId,
            receiverId,
            "has requested to join your trip "+ tripName,
            "TRIP",
            tripId,
            Map.of("tripId", tripId, "tripName", tripName),
            System.currentTimeMillis()
        );

        kafkaTemplate.send("notification-events", receiverId, event);
    }

     @Override
    public void acceptTripRequestNotification(String senderId, String receiverId, String tripId,String tripName) {

         NotificationEvent event = new NotificationEvent(
            "TRIP_REQUEST_ACCEPTED",
            senderId,
            receiverId,
            "has accepted your trip request",
            "TRIP",
            tripId,
            Map.of("tripId", tripId, "tripName", tripName),
            System.currentTimeMillis()
        );

        kafkaTemplate.send("notification-events", receiverId, event);
    }
    
}

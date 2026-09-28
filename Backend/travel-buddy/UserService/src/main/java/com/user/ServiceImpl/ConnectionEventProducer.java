package com.user.ServiceImpl;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.events.Connection.ConnectionCreatedEvent;
import com.events.Connection.ConnectionRemovedEvent;

@Service
public class ConnectionEventProducer {

    private static final String TOPIC = "connection-events";

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public ConnectionEventProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishCreated(String followerId, String followingId) {
        ConnectionCreatedEvent event = ConnectionCreatedEvent.builder()
                .followerId(followerId)
                .followingId(followingId)
                .build();
        kafkaTemplate.send(TOPIC, followerId, event);
    }

    public void publishRemoved(String followerId, String followingId) {
        ConnectionRemovedEvent event = ConnectionRemovedEvent.builder()
                .followerId(followerId)
                .followingId(followingId)
                .build();
        kafkaTemplate.send(TOPIC, followerId, event);
    }
}
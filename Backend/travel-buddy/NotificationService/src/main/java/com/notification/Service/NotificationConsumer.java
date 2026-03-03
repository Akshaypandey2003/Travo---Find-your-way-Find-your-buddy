package com.notification.Service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.events.Notification.NotificationEvent;
import com.notification.handler.NotificationHandler;

@Service
public class NotificationConsumer {

    private static final Logger logger = LoggerFactory.getLogger(NotificationConsumer.class);
    private final List<NotificationHandler> handlers;

    public NotificationConsumer(List<NotificationHandler> handlers) {
        this.handlers = handlers;
    }

    @KafkaListener(
            topics = "${app.kafka.topics.notification-events:notification-events}",
            groupId = "${spring.kafka.consumer.group-id:notification-service}")
    public void consume(NotificationEvent event) {

        logger.info("Received notification event type={} receiverId={}", event.getType(), event.getReceiverId());

        try {
            handlers.stream()
                    .filter(handler -> handler.supports(event))
                    .findFirst()
                    .ifPresentOrElse(
                            handler -> handler.handle(event),
                            () -> logger.warn("No handler found for event type={}", event.getType()));
        } catch (Exception ex) {
            logger.error("Failed processing notification event type={} receiverId={}", event.getType(), event.getReceiverId(), ex);
            throw ex;
        }
    }
}

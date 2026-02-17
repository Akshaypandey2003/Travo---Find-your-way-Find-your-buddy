package com.notification.Service;

import java.util.List;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.events.Entity.NotificationEvent;
import com.notification.handler.NotificationHandler;

@Service
public class NotificationConsumer {

    private final List<NotificationHandler> handlers;

    public NotificationConsumer(List<NotificationHandler> handlers) {
        this.handlers = handlers;
    }

    @KafkaListener(topics = "notification-events")
    public void consume(NotificationEvent event) {

        System.out.println("Received event: " + event);

        handlers.stream()
                .filter(handler -> handler.supports(event))
                .findFirst()
                .ifPresentOrElse(
                        handler -> handler.handle(event),
                        () -> System.out.println(
                                "No handler found for event type: "
                                        + event.getType()
                        )
                );
    }
}
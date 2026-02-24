package com.notification.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.events.Notification.NotificationDeleteEvent;

@Component
public class TripDomainEventConsumer {

    private static final Logger logger = LoggerFactory.getLogger(TripDomainEventConsumer.class);

    private final NotificationService notificationService;

    public TripDomainEventConsumer(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @KafkaListener(
            topics = "${app.kafka.topics.trip-events:trip-events}",
            groupId = "${spring.kafka.consumer.group-id:notification-service}")
    public void consume(Object event) {
        try {
            if (event instanceof NotificationDeleteEvent deleteEvent) {
                handleNotificationDelete(deleteEvent);
            }
        } catch (Exception ex) {
            logger.error("Failed processing trip domain event={}", event, ex);
            throw ex;
        }
    }

    private void handleNotificationDelete(NotificationDeleteEvent event) {
        String notificationId = event.getNotificationId();
        boolean deleted = notificationService.deleteNotificationIfExists(notificationId);

        if (deleted) {
            logger.info("Deleted notification id={} from NOTIFICATION_DELETE domain event by={}", notificationId, event.getFrom());
        } else {
            logger.info("Notification id={} not found; NOTIFICATION_DELETE treated as idempotent", notificationId);
        }
    }
}

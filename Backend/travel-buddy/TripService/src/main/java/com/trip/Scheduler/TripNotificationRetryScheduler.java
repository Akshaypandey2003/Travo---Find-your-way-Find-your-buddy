package com.trip.Scheduler;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.trip.Entity.FailedNotification;
import com.trip.Repositories.FailedNotificationRepository;

@Component
public class TripNotificationRetryScheduler {

    private static final Logger logger = LoggerFactory.getLogger(TripNotificationRetryScheduler.class);

    private final FailedNotificationRepository failedNotificationRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${trip.notifications.retry.max-attempts:10}")
    private int maxRetryAttempts;

    @Value("${trip.notifications.retry.batch-size:50}")
    private int batchSize;

    public TripNotificationRetryScheduler(
            FailedNotificationRepository failedNotificationRepository,
            KafkaTemplate<String, Object> kafkaTemplate) {
        this.failedNotificationRepository = failedNotificationRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Scheduled(fixedDelayString = "${trip.notifications.retry.fixed-delay-ms:30000}")
    public void retryFailedNotifications() {
        Page<FailedNotification> page = failedNotificationRepository.findAllByOrderByCreatedAtAsc(
                PageRequest.of(0, batchSize));

        List<FailedNotification> failedList = page.getContent();
        if (failedList.isEmpty()) {
            return;
        }

        for (FailedNotification failed : failedList) {
            try {
                kafkaTemplate.send(failed.getTopic(), failed.getKey(), failed.getEvent()).get();
                failedNotificationRepository.deleteById(failed.getId());
                logger.info("Retried failed notification successfully id={}", failed.getId());
            } catch (Exception ex) {
                int retryCount = failed.getRetryCount() + 1;
                if (retryCount >= maxRetryAttempts) {
                    failedNotificationRepository.deleteById(failed.getId());
                    logger.error("Dropping failed notification id={} after {} retries", failed.getId(), maxRetryAttempts, ex);
                } else {
                    failed.setRetryCount(retryCount);
                    failed.setLastRetryAt(System.currentTimeMillis());
                    failed.setFailureReason(ex.getMessage());
                    failedNotificationRepository.save(failed);
                    logger.warn("Retry failed for notification id={} attempt={}", failed.getId(), retryCount);
                }
            }
        }
    }
}

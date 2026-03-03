package com.user.Scheduler;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.events.Notification.FailedNotification;
import com.events.Repositories.FailedNotificationRepository;

@Component
public class NotificationRetryScheduler {

    private final FailedNotificationRepository failedRepo;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final Logger logger = LoggerFactory.getLogger(NotificationRetryScheduler.class);
   
    @Value("${user.notifications.retry.max-attempts:10}")
    private int MAX_RETRY;

    @Value("${user.notifications.retry.batch-size:50}")
    private int BATCH_SIZE;

    public NotificationRetryScheduler(
            FailedNotificationRepository failedRepo,
            KafkaTemplate<String, Object> kafkaTemplate) {

        this.failedRepo = failedRepo;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Scheduled(fixedDelayString = "${user.notifications.retry.fixed-delay-ms:30000}")
    public void retryFailedNotifications() {

        Page<FailedNotification> page = failedRepo.findAllByOrderByCreatedAtAsc(
                PageRequest.of(0, BATCH_SIZE));

        List<FailedNotification> failedList = page.getContent();

        for (FailedNotification failed : failedList) {

            try {

                kafkaTemplate.send(
                        failed.getTopic(),
                        failed.getKey(),
                        failed.getEvent())
                        .get();

                failedRepo.deleteById(failed.getId());

                logger.info("Retry success for notification {}",
                        failed.getId());

            } catch (Exception e) {

                int newRetryCount = failed.getRetryCount() + 1;

                if (newRetryCount >= MAX_RETRY) {

                    failedRepo.deleteById(failed.getId());

                    logger.error(
                            "Moved notification {} to DEAD state after {} retries",
                            failed.getId(),
                            MAX_RETRY);

                } else {

                    failed.setRetryCount(newRetryCount);
                    failedRepo.save(failed);

                    logger.warn(
                            "Retry failed for {}. Attempt {}",
                            failed.getId(),
                            newRetryCount);
                }
            }
        }
    }
}

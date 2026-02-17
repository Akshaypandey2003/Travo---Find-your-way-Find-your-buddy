package com.user.Scheduler;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.user.Entity.FailedNotification;
import com.user.Repository.FailedNotificationRepo;

@Component
public class NotificationRetryScheduler {

    private final FailedNotificationRepo failedRepo;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final Logger logger = LoggerFactory.getLogger(NotificationRetryScheduler.class);

    private static final int MAX_RETRY = 10;
    private static final int BATCH_SIZE = 50;

    public NotificationRetryScheduler(
            FailedNotificationRepo failedRepo,
            KafkaTemplate<String, Object> kafkaTemplate) {

        this.failedRepo = failedRepo;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Scheduled(fixedDelay = 30000)
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

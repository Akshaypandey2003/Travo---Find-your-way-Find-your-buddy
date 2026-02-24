package com.feedback.Scheduler;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.feedback.Entity.FailedOutboundEvent;
import com.feedback.Repository.FailedOutboundEventRepo;

@Component
public class OutboundEventRetryScheduler {

    private static final Logger logger = LoggerFactory.getLogger(OutboundEventRetryScheduler.class);

    private final FailedOutboundEventRepo failedOutboundEventRepo;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${feedback.outbox.retry.max-attempts:10}")
    private int maxAttempts;

    @Value("${feedback.outbox.retry.batch-size:50}")
    private int batchSize;

    public OutboundEventRetryScheduler(
            FailedOutboundEventRepo failedOutboundEventRepo,
            KafkaTemplate<String, Object> kafkaTemplate) {
        this.failedOutboundEventRepo = failedOutboundEventRepo;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Scheduled(fixedDelayString = "${feedback.outbox.retry.fixed-delay-ms:30000}")
    public void retryFailedOutboundEvents() {
        Page<FailedOutboundEvent> page = failedOutboundEventRepo.findAllByOrderByCreatedAtAsc(PageRequest.of(0, batchSize));
        List<FailedOutboundEvent> events = page.getContent();

        for (FailedOutboundEvent event : events) {
            try {
                kafkaTemplate.send(event.getTopic(), event.getEventKey(), event.getPayload()).get();
                failedOutboundEventRepo.deleteById(event.getId());
                logger.info("Retried outbound event successfully id={} topic={}", event.getId(), event.getTopic());
            } catch (Exception ex) {
                int retryCount = event.getRetryCount() + 1;
                if (retryCount >= maxAttempts) {
                    failedOutboundEventRepo.deleteById(event.getId());
                    logger.error("Dropping outbound event id={} after {} attempts", event.getId(), maxAttempts, ex);
                } else {
                    event.setRetryCount(retryCount);
                    event.setLastRetryAt(System.currentTimeMillis());
                    event.setFailureReason(ex.getMessage());
                    failedOutboundEventRepo.save(event);
                    logger.warn("Retry failed for outbound event id={} attempt={}", event.getId(), retryCount);
                }
            }
        }
    }
}

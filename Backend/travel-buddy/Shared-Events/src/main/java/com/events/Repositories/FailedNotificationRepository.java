package com.events.Repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.events.Notification.FailedNotification;

public interface FailedNotificationRepository extends MongoRepository<FailedNotification, String> {
    Page<FailedNotification> findAllByOrderByCreatedAtAsc(Pageable pageable);
}

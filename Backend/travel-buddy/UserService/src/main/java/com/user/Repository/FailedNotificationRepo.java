package com.user.Repository;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import com.user.Entity.FailedNotification;

public interface FailedNotificationRepo
        extends MongoRepository<FailedNotification, String> {
            Page<FailedNotification> findAllByOrderByCreatedAtAsc(Pageable pageable);
}
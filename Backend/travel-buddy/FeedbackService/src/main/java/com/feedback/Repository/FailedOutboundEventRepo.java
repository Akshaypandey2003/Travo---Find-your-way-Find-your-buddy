package com.feedback.Repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.feedback.Entity.FailedOutboundEvent;

public interface FailedOutboundEventRepo extends MongoRepository<FailedOutboundEvent, String> {
    Page<FailedOutboundEvent> findAllByOrderByCreatedAtAsc(Pageable pageable);
}

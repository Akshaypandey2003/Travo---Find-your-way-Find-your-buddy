package com.feedback.Repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.feedback.Entity.AppFeedback;

public interface AppFeedbackRepo extends MongoRepository<AppFeedback, String> {
    Page<AppFeedback> findByUserId(String userId, Pageable pageable);
}

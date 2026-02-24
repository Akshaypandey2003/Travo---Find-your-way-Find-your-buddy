package com.user.Repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.user.Entity.ProcessedFeedbackEvent;

public interface ProcessedFeedbackEventRepo extends MongoRepository<ProcessedFeedbackEvent, String> {
}

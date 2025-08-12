package com.feedback.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.feedback.Entity.FeedBack;

@Repository
public interface FeedbackRepo extends MongoRepository<FeedBack,String> {
      Optional<FeedBack> findByTripIdAndAuthorId(String tripId, String userId);

    List<FeedBack> findAllByTripId(String tripId);
}

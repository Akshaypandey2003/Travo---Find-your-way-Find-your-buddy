package com.feedback.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.feedback.Entity.CompanionReview;

public interface CompanionReviewRepo extends MongoRepository<CompanionReview, String> {
    Optional<CompanionReview> findByTripIdAndReviewerUserIdAndTargetUserId(
            String tripId,
            String reviewerUserId,
            String targetUserId);

    long countByTripIdAndReviewerUserId(String tripId, String reviewerUserId);

    List<CompanionReview> findByTripIdAndReviewerUserId(String tripId, String reviewerUserId);

    Page<CompanionReview> findByTripId(String tripId, Pageable pageable);

    Page<CompanionReview> findByTripIdAndTargetUserId(String tripId, String targetUserId, Pageable pageable);
}

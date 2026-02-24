package com.feedback.ServiceImpl;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.feedback.Clients.TripClient;
import com.feedback.DTO.AppFeedbackResponse;
import com.feedback.DTO.CompanionReviewResponse;
import com.feedback.DTO.PageResponseDto;
import com.feedback.DTO.SubmitAppFeedbackRequest;
import com.feedback.DTO.SubmitCompanionReviewRequest;
import com.feedback.DTO.SuggestedCompanionResponse;
import com.feedback.DTO.TripSummary;
import com.feedback.Entity.AppFeedback;
import com.feedback.Entity.CompanionReview;
import com.feedback.Exceptions.FeedbackAlreadySubmittedException;
import com.feedback.Exceptions.InvalidCompanionReviewException;
import com.feedback.Exceptions.ReviewLimitExceededException;
import com.feedback.Repository.AppFeedbackRepo;
import com.feedback.Repository.CompanionReviewRepo;
import com.feedback.Service.FeedbackDomainEventPublisher;
import com.feedback.Service.FeedbackService;
import com.feedback.Service.NotificationProducer;

@Service
public class FeedbackServiceImpl implements FeedbackService {

    private final CompanionReviewRepo companionReviewRepo;
    private final AppFeedbackRepo appFeedbackRepo;
    private final TripClient tripClient;
    private final NotificationProducer notificationProducer;
    private final FeedbackDomainEventPublisher feedbackDomainEventPublisher;

    @Value("${feedback.companion.max-peer-reviews-per-trip:3}")
    private int maxPeerReviewsPerTrip;

    @Value("${feedback.pagination.max-page-size:100}")
    private int maxPageSize;

    public FeedbackServiceImpl(
            CompanionReviewRepo companionReviewRepo,
            AppFeedbackRepo appFeedbackRepo,
            TripClient tripClient,
            NotificationProducer notificationProducer,
            FeedbackDomainEventPublisher feedbackDomainEventPublisher) {
        this.companionReviewRepo = companionReviewRepo;
        this.appFeedbackRepo = appFeedbackRepo;
        this.tripClient = tripClient;
        this.notificationProducer = notificationProducer;
        this.feedbackDomainEventPublisher = feedbackDomainEventPublisher;
    }

    @Override
    public CompanionReviewResponse submitCompanionReview(SubmitCompanionReviewRequest request) {
        if (request.getReviewerUserId().equals(request.getTargetUserId())) {
            throw new InvalidCompanionReviewException("You cannot submit companion review for yourself.");
        }

        TripSummary tripSummary = tripClient.getTripSummaryById(request.getTripId());
        validateTripMembership(tripSummary, request.getReviewerUserId(), request.getTargetUserId());

        companionReviewRepo.findByTripIdAndReviewerUserIdAndTargetUserId(
                request.getTripId(),
                request.getReviewerUserId(),
                request.getTargetUserId()).ifPresent(existing -> {
                    throw new FeedbackAlreadySubmittedException("Companion review already submitted for this user in this trip.");
                });

        long submittedCount = companionReviewRepo.countByTripIdAndReviewerUserId(request.getTripId(), request.getReviewerUserId());
        if (submittedCount >= maxPeerReviewsPerTrip) {
            throw new ReviewLimitExceededException(
                    "Review limit reached. You can submit at most " + maxPeerReviewsPerTrip + " companion reviews per trip.");
        }

        CompanionReview review = CompanionReview.builder()
                .tripId(request.getTripId())
                .reviewerUserId(request.getReviewerUserId())
                .targetUserId(request.getTargetUserId())
                .rating(request.getRating())
                .review(request.getReview())
                .tags(request.getTags() == null ? new LinkedHashSet<>() : new LinkedHashSet<>(request.getTags()))
                .build();

        CompanionReview saved = companionReviewRepo.save(review);
        feedbackDomainEventPublisher.publishCompanionReviewSubmitted(saved);
        notificationProducer.sendCompanionReviewNotification(
                saved.getReviewerUserId(),
                saved.getTargetUserId(),
                saved.getTripId(),
                tripSummary.getTripName());

        return CompanionReviewResponse.from(saved);
    }

    @Override
    public PageResponseDto<CompanionReviewResponse> getTripCompanionReviews(
            String tripId,
            String targetUserId,
            int page,
            int size,
            String sortBy,
            String direction) {
        Pageable pageable = buildPageable(page, size, sortBy, direction);
        Page<CompanionReview> reviewPage = (targetUserId == null || targetUserId.isBlank())
                ? companionReviewRepo.findByTripId(tripId, pageable)
                : companionReviewRepo.findByTripIdAndTargetUserId(tripId, targetUserId, pageable);

        List<CompanionReviewResponse> items = reviewPage.getContent().stream()
                .map(CompanionReviewResponse::from)
                .toList();

        return PageResponseDto.<CompanionReviewResponse>builder()
                .items(items)
                .page(reviewPage.getNumber())
                .size(reviewPage.getSize())
                .totalElements(reviewPage.getTotalElements())
                .totalPages(reviewPage.getTotalPages())
                .first(reviewPage.isFirst())
                .last(reviewPage.isLast())
                .hasNext(reviewPage.hasNext())
                .hasPrevious(reviewPage.hasPrevious())
                .build();
    }

    @Override
    public boolean hasUserAlreadySubmittedCompanionReview(String tripId, String reviewerUserId, String targetUserId) {
        return companionReviewRepo.findByTripIdAndReviewerUserIdAndTargetUserId(tripId, reviewerUserId, targetUserId).isPresent();
    }

    @Override
    public SuggestedCompanionResponse getSuggestedCompanionsForReview(String tripId, String reviewerUserId, int suggestionLimit) {
        TripSummary tripSummary = tripClient.getTripSummaryById(tripId);
        if (tripSummary == null || tripSummary.getMembers() == null || tripSummary.getMembers().isEmpty()) {
            return SuggestedCompanionResponse.builder()
                    .tripId(tripId)
                    .reviewerUserId(reviewerUserId)
                    .maxReviewsAllowed(maxPeerReviewsPerTrip)
                    .reviewsSubmitted(0)
                    .reviewsRemaining(maxPeerReviewsPerTrip)
                    .suggestedTargetUserIds(List.of())
                    .build();
        }

        List<CompanionReview> alreadyReviewed = companionReviewRepo.findByTripIdAndReviewerUserId(tripId, reviewerUserId);
        Set<String> reviewedTargets = alreadyReviewed.stream()
                .map(CompanionReview::getTargetUserId)
                .collect(java.util.stream.Collectors.toSet());

        long submitted = alreadyReviewed.size();
        long remaining = Math.max(maxPeerReviewsPerTrip - submitted, 0);
        int limit = suggestionLimit <= 0 ? 5 : suggestionLimit;
        limit = Math.min(limit, (int) remaining);

        List<String> suggestions = tripSummary.getMembers().stream()
                .filter(memberId -> !reviewerUserId.equals(memberId))
                .filter(memberId -> !reviewedTargets.contains(memberId))
                .limit(limit)
                .toList();

        return SuggestedCompanionResponse.builder()
                .tripId(tripId)
                .reviewerUserId(reviewerUserId)
                .maxReviewsAllowed(maxPeerReviewsPerTrip)
                .reviewsSubmitted(submitted)
                .reviewsRemaining(remaining)
                .suggestedTargetUserIds(suggestions)
                .build();
    }

    @Override
    public AppFeedbackResponse submitAppFeedback(SubmitAppFeedbackRequest request) {
        AppFeedback feedback = AppFeedback.builder()
                .userId(request.getUserId())
                .tripId(request.getTripId())
                .rating(request.getRating())
                .comment(request.getComment())
                .featuresLiked(request.getFeaturesLiked() == null ? new LinkedHashSet<>() : new LinkedHashSet<>(request.getFeaturesLiked()))
                .build();

        AppFeedback saved = appFeedbackRepo.save(feedback);
        feedbackDomainEventPublisher.publishAppFeedbackSubmitted(saved);
        return AppFeedbackResponse.from(saved);
    }

    @Override
    public PageResponseDto<AppFeedbackResponse> getAppFeedbackByUser(String userId, int page, int size) {
        Pageable pageable = buildPageable(page, size, "createdAt", "desc");
        Page<AppFeedback> feedbackPage = appFeedbackRepo.findByUserId(userId, pageable);

        List<AppFeedbackResponse> items = feedbackPage.getContent().stream()
                .map(AppFeedbackResponse::from)
                .toList();

        return PageResponseDto.<AppFeedbackResponse>builder()
                .items(items)
                .page(feedbackPage.getNumber())
                .size(feedbackPage.getSize())
                .totalElements(feedbackPage.getTotalElements())
                .totalPages(feedbackPage.getTotalPages())
                .first(feedbackPage.isFirst())
                .last(feedbackPage.isLast())
                .hasNext(feedbackPage.hasNext())
                .hasPrevious(feedbackPage.hasPrevious())
                .build();
    }

    private void validateTripMembership(TripSummary summary, String reviewerUserId, String targetUserId) {
        if (summary == null || summary.getMembers() == null || summary.getMembers().isEmpty()) {
            throw new InvalidCompanionReviewException("Trip members data not available.");
        }
        if (!summary.getMembers().contains(reviewerUserId)) {
            throw new InvalidCompanionReviewException("Reviewer is not a trip member.");
        }
        if (!summary.getMembers().contains(targetUserId)) {
            throw new InvalidCompanionReviewException("Target user is not a trip member.");
        }
    }

    private Pageable buildPageable(int page, int size, String sortBy, String direction) {
        int pageNumber = Math.max(page, 0);
        int pageSize = Math.min(Math.max(size, 1), maxPageSize);
        Sort.Direction sortDirection = "asc".equalsIgnoreCase(direction) ? Sort.Direction.ASC : Sort.Direction.DESC;
        String safeSortBy = (sortBy == null || sortBy.isBlank()) ? "createdAt" : sortBy;
        return PageRequest.of(pageNumber, pageSize, Sort.by(sortDirection, safeSortBy));
    }
}

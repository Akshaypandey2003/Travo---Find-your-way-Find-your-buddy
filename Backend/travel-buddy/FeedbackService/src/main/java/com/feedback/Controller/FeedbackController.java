package com.feedback.Controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.feedback.DTO.AppFeedbackResponse;
import com.feedback.DTO.CompanionReviewResponse;
import com.feedback.DTO.PageResponseDto;
import com.feedback.DTO.SubmitAppFeedbackRequest;
import com.feedback.DTO.SubmitCompanionReviewRequest;
import com.feedback.DTO.SuggestedCompanionResponse;
import com.feedback.Service.FeedbackService;

@RestController
@RequestMapping("/api/v1/feedback")
@Validated
public class FeedbackController {

    private final FeedbackService feedbackService;

    public FeedbackController(FeedbackService feedbackService) {
        this.feedbackService = feedbackService;
    }

    @PostMapping("/companion-reviews")
    public ResponseEntity<CompanionReviewResponse> submitCompanionReview(
            @Valid @RequestBody SubmitCompanionReviewRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(feedbackService.submitCompanionReview(request));
    }

    @GetMapping("/trips/{tripId}/companion-reviews")
    public ResponseEntity<PageResponseDto<CompanionReviewResponse>> getTripCompanionReviews(
            @PathVariable String tripId,
            @RequestParam(required = false) String targetUserId,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {
        return ResponseEntity.ok(feedbackService.getTripCompanionReviews(tripId, targetUserId, page, size, sortBy, direction));
    }

    @GetMapping("/companion-reviews/check/{tripId}/{reviewerUserId}/{targetUserId}")
    public ResponseEntity<Boolean> hasSubmittedCompanionReview(
            @PathVariable String tripId,
            @PathVariable String reviewerUserId,
            @PathVariable String targetUserId) {
        return ResponseEntity.ok(feedbackService.hasUserAlreadySubmittedCompanionReview(tripId, reviewerUserId, targetUserId));
    }

    @GetMapping("/trips/{tripId}/suggested-companions/{reviewerUserId}")
    public ResponseEntity<SuggestedCompanionResponse> getSuggestedCompanionsForReview(
            @PathVariable String tripId,
            @PathVariable String reviewerUserId,
            @RequestParam(defaultValue = "5") @Min(1) @Max(20) int suggestionLimit) {
        return ResponseEntity.ok(feedbackService.getSuggestedCompanionsForReview(tripId, reviewerUserId, suggestionLimit));
    }

    @PostMapping("/app-feedback")
    public ResponseEntity<AppFeedbackResponse> submitAppFeedback(
            @Valid @RequestBody SubmitAppFeedbackRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(feedbackService.submitAppFeedback(request));
    }

    @GetMapping("/app-feedback/users/{userId}")
    public ResponseEntity<PageResponseDto<AppFeedbackResponse>> getAppFeedbackByUser(
            @PathVariable String userId,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size) {
        return ResponseEntity.ok(feedbackService.getAppFeedbackByUser(userId, page, size));
    }
}

package com.feedback.Exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    
      @ExceptionHandler(FeedbackAlreadySubmittedException.class)
    public ResponseEntity<String> handleDuplicateFeedback(
            FeedbackAlreadySubmittedException ex) {

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ex.getMessage());
    }

    @ExceptionHandler(ReviewLimitExceededException.class)
    public ResponseEntity<String> handleReviewLimitExceeded(
            ReviewLimitExceededException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(InvalidCompanionReviewException.class)
    public ResponseEntity<String> handleInvalidCompanionReview(
            InvalidCompanionReviewException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }
}

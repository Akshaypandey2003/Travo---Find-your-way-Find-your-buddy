package com.feedback.Exceptions;

public class FeedbackAlreadySubmittedException extends RuntimeException {
    public FeedbackAlreadySubmittedException(String message) {
        super(message);
    }
}
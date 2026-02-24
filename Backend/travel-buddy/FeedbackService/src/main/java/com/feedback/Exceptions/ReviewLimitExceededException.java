package com.feedback.Exceptions;

public class ReviewLimitExceededException extends RuntimeException {
    public ReviewLimitExceededException(String message) {
        super(message);
    }
}

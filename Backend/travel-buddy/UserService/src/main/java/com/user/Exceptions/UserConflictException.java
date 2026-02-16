package com.user.Exceptions;

public class UserConflictException  extends RuntimeException {
    public UserConflictException(String message) {
        super(message);
    }
}
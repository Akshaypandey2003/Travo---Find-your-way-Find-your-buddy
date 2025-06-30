package com.chat.Exceptions;

public class ChatNotFoundException extends RuntimeException {
    
    public ChatNotFoundException(String message) {
        super(message);
    }
    public ChatNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}

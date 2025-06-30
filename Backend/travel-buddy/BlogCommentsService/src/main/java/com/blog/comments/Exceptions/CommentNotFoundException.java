package com.blog.comments.Exceptions;

public class CommentNotFoundException extends RuntimeException 
{
    public CommentNotFoundException(String message) {
        super(message);
    }
    public CommentNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
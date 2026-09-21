package com.user.Exceptions;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.user.DTO.MessageResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<MessageResponse> handleUserNotFoundException(UserNotFoundException ex) {
        MessageResponse response = new MessageResponse(
                ex.getMessage(),
                "error"
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(UserConflictException.class)
    public ResponseEntity<MessageResponse> handleUserConflictException(UserConflictException ex) {
         MessageResponse response = new MessageResponse(
                ex.getMessage(),
                "error"
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler(InvalidPasswordException.class)
    public ResponseEntity<MessageResponse> handleInvalidPasswordException(InvalidPasswordException ex) {

          MessageResponse response = new MessageResponse(
                ex.getMessage(),
                "error"
        );
        return  ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    @ExceptionHandler(ConnectionRequestException.class)
    public ResponseEntity<MessageResponse> handleConnectionRequestException(ConnectionRequestException ex) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new MessageResponse(ex.getMessage(), "error"));
    }

    @ExceptionHandler(InvalidResetTokenException.class)
    public ResponseEntity<MessageResponse> handleInvalidToken(
            InvalidResetTokenException ex) {

        return ResponseEntity.badRequest()
                .body(new MessageResponse(ex.getMessage(), "failure"));
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<MessageResponse> handleResourceNotFound(
            InvalidResetTokenException ex) {

        return ResponseEntity.badRequest()
                .body(new MessageResponse(ex.getMessage(), "failure"));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationException(
            MethodArgumentNotValidException ex) {

        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult()
                .getFieldErrors()
                .forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(errors);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGenericException(Exception ex) {
        return new ResponseEntity<>("An error occurred: " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

}

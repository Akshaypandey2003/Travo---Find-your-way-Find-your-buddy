package com.room.Exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;

import com.room.DTO.ErrorResponse;

@RestController
public class GlobalExceptionHandler {

    @ExceptionHandler( Exception.class )
    public String handleGlobalException( Exception ex ) {
        return "An unexpected error occurred: " + ex.getMessage();
    }

    @ExceptionHandler( ResourceNotFoundException.class )
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException( ResourceNotFoundException ex ) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setMessage( ex.getMessage() );
        errorResponse.setHttpStatus( HttpStatus.NOT_FOUND );
        errorResponse.setSuccess( false );
        return ResponseEntity.status( HttpStatus.NOT_FOUND ).body( errorResponse );
    }    
}

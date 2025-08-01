package com.flux.lms.exceptions;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.logging.Logger;

@ControllerAdvice
public class GlobalExceptionHandler {
    
    private static final Logger LOGGER = Logger.getLogger(GlobalExceptionHandler.class.getName());
    
    @ExceptionHandler(DuplicateEmailException.class)
    public ResponseEntity<String> handleDuplicateEmail(DuplicateEmailException e) {
        LOGGER.warning("Duplicate email exception: " + e.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
    }
    
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<String> handleDataIntegrityViolation(DataIntegrityViolationException e) {
        String message = "This information is already registered in our system.";
        
        if (e.getMessage().contains("users_email_key")) {
            message = "This email address is already registered in our system.";
        } else if (e.getMessage().contains("users_username_key")) {
            message = "This username is already taken. Please choose a different username.";
        } else if (e.getMessage().contains("instructors_phone_key")) {
            message = "This phone number is already registered. Please use a different phone number.";
        }
        
        LOGGER.warning("Data integrity violation: " + e.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(message);
    }
}
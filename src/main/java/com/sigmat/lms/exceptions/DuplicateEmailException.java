package com.sigmat.lms.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class DuplicateEmailException extends RuntimeException {
    
    public DuplicateEmailException(String email) {
        super("This email address is already registered in our system.");
    }
    
    public DuplicateEmailException(String message, Throwable cause) {
        super(message, cause);
    }
}
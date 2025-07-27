package com.sigmat.lms.exceptions;

public class InstructorProfileNotFoundException extends RuntimeException {
    public InstructorProfileNotFoundException(String message) {
        super(message);
    }
}
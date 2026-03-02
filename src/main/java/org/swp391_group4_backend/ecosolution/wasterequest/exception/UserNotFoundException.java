package org.swp391_group4_backend.ecosolution.wasterequest.exception;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String message) {
        super(message);
    }
}


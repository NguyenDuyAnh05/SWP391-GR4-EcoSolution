package org.swp391_group4_backend.ecosolution.wasterequest.exception;

public class InvalidStateTransitionException extends RuntimeException {
    public InvalidStateTransitionException(String message) {
        super(message);
    }
}


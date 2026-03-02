package org.swp391_group4_backend.ecosolution.wasterequest.exception;

public class DailyRequestLimitExceededException extends RuntimeException {
    public DailyRequestLimitExceededException(String message) {
        super(message);
    }
}


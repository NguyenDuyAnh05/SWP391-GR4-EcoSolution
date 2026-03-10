package org.swp391_group4_backend.ecosolution.fraud.exception;

import java.util.UUID;

public class FraudSignalNotFoundException extends RuntimeException {
    public FraudSignalNotFoundException(UUID id) {
        super("Fraud signal not found with ID: " + id);
    }
}


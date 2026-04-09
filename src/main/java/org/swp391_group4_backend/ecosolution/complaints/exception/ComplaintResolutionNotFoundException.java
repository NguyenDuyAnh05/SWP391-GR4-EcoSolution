package org.swp391_group4_backend.ecosolution.complaints.exception;

import java.util.UUID;

public class ComplaintResolutionNotFoundException extends RuntimeException {
    public ComplaintResolutionNotFoundException(UUID id) {
        super("Complaint resolution not found with ID: " + id);
    }
}


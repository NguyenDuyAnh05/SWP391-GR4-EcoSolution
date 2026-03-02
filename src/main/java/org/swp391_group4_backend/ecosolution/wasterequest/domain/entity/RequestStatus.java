package org.swp391_group4_backend.ecosolution.wasterequest.domain.entity;

// BR15: No REJECTED status. BR24: Strict lifecycle transitions.
public enum RequestStatus {
    PENDING,
    ASSIGNED,
    ACCEPTED,
    IN_PROGRESS,
    COMPLETED,
    CANCELLED
}

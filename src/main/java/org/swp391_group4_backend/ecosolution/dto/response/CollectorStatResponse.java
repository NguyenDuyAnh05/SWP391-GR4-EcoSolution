package org.swp391_group4_backend.ecosolution.dto.response;

public record CollectorStatResponse(
        Long collectorId,
        String firstName,
        String lastName,
        Long totalCollected
) {
}

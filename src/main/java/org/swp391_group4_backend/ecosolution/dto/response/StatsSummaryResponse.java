package org.swp391_group4_backend.ecosolution.dto.response;

public record StatsSummaryResponse(
        long totalPending,
        long totalAssigned,
        long totalInProgress,
        long totalCollected
) {
}

package org.swp391_group4_backend.ecosolution.auth.domain.dto.request;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CollectingTaskAssignRequestDto(
        @NotNull(message = "Report ID is required")
        UUID reportId,

        @NotNull(message = "Collector ID is required")
        UUID collectorId
) {
}


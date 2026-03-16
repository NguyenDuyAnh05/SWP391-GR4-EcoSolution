package org.swp391_group4_backend.ecosolution.dto.request;

import jakarta.validation.constraints.NotNull;

public record AssignCollectorRequest(
        @NotNull(message = "Report ID is required")
        Long reportId,
        @NotNull(message = "Collector ID is required")
        Long collectorId
) {
}

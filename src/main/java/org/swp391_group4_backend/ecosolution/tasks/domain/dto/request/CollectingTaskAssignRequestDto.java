package org.swp391_group4_backend.ecosolution.tasks.domain.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

@Schema(name = "CollectingTaskAssignRequest", description = "Payload used to assign a collector to a report")
public record CollectingTaskAssignRequestDto(
        @Schema(description = "Report identifier", example = "11111111-2222-3333-4444-555555555555")
        @NotNull(message = "Report ID is required")
        UUID reportId,

        @Schema(description = "Collector user identifier", example = "aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee")
        @NotNull(message = "Collector ID is required")
        UUID collectorId
) {
}

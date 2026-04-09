package org.swp391_group4_backend.ecosolution.tasks.domain.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import org.swp391_group4_backend.ecosolution.tasks.domain.entity.TaskStatus;

import java.time.LocalDateTime;
import java.util.UUID;

@Schema(name = "CollectingTaskResponse", description = "Task details returned by task APIs")
public record CollectingTaskResponseDto(
        @Schema(description = "Task identifier", example = "99999999-8888-7777-6666-555555555555")
        UUID id,
        @Schema(description = "Related report identifier", example = "11111111-2222-3333-4444-555555555555")
        UUID reportId,
        @Schema(description = "Assigned collector identifier", example = "aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee")
        UUID collectorId,
        @Schema(description = "Assigned collector display name", example = "John Collector")
        String collectorName,
        @Schema(description = "Current lifecycle status", example = "ASSIGNED")
        TaskStatus currentStatus,
        @Schema(description = "Assignment timestamp (ISO-8601)", example = "2026-03-07T10:30:00")
        LocalDateTime assignedAt,
        @Schema(description = "Task start timestamp (ISO-8601)", example = "2026-03-07T11:00:00")
        LocalDateTime startedAt,
        @Schema(description = "Task completion timestamp (ISO-8601)", example = "2026-03-07T12:15:00")
        LocalDateTime completedAt
) {
}

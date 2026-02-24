package org.swp391_group4_backend.ecosolution.tasks.domain.dto.response;

import org.swp391_group4_backend.ecosolution.tasks.domain.entity.TaskStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record CollectingTaskResponseDto(
        UUID id,
        UUID reportId,
        UUID collectorId,
        String collectorName,
        TaskStatus currentStatus,
        LocalDateTime assignedAt,
        LocalDateTime startedAt,
        LocalDateTime completedAt
) {
}




package org.swp391_group4_backend.ecosolution.collectors.domain.dto.response;

import org.swp391_group4_backend.ecosolution.tasks.domain.entity.TaskStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record CollectorStatusHistoryResponseDto(
    UUID id,
    UUID collectorId,
    TaskStatus statusFrom,
    TaskStatus statusTo,
    String reason,
    LocalDateTime changedAt
) {
}

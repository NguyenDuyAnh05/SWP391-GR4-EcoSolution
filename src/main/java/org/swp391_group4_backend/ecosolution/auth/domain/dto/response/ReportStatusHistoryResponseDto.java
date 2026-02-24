package org.swp391_group4_backend.ecosolution.auth.domain.dto.response;

import org.swp391_group4_backend.ecosolution.auth.domain.entity.ReportStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record ReportStatusHistoryResponseDto(
        UUID id,
        UUID reportId,
        ReportStatus statusFrom,
        ReportStatus statusTo,
        UUID changedById,
        String changedByName,
        String reason,
        LocalDateTime changedAt
) {
}


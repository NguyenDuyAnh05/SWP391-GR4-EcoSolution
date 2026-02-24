package org.swp391_group4_backend.ecosolution.auth.domain.dto.response;

import org.swp391_group4_backend.ecosolution.auth.domain.entity.ComplaintStatus;
import org.swp391_group4_backend.ecosolution.auth.domain.entity.ComplaintType;

import java.time.LocalDateTime;
import java.util.UUID;

public record ComplaintResponseDto(
        UUID id,
        UUID reportId,
        UUID citizenId,
        String citizenName,
        ComplaintType type,
        ComplaintStatus status,
        String description,
        LocalDateTime createdAt,
        LocalDateTime resolvedAt
) {
}


package org.swp391_group4_backend.ecosolution.auth.domain.dto.response;

import org.swp391_group4_backend.ecosolution.auth.domain.entity.FraudType;

import java.time.LocalDateTime;
import java.util.UUID;

public record FraudSignalResponseDto(
        UUID id,
        UUID citizenId,
        String citizenName,
        UUID reportId,
        FraudType type,
        Integer score,
        LocalDateTime createdAt
) {
}


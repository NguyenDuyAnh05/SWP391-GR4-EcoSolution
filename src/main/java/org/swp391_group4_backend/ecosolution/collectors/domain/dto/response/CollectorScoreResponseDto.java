package org.swp391_group4_backend.ecosolution.collectors.domain.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record CollectorScoreResponseDto(
        UUID collectorId,
        String collectorName,
        BigDecimal responseRate,
        BigDecimal completionRate,
        BigDecimal complaintRate,
        BigDecimal reliabilityScore,
        LocalDateTime updatedAt
) {
}



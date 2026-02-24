package org.swp391_group4_backend.ecosolution.auth.domain.dto.response;

import org.swp391_group4_backend.ecosolution.auth.domain.entity.CancelReasonCode;
import org.swp391_group4_backend.ecosolution.auth.domain.entity.ReportStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record WasteReportResponseDto(
        UUID id,
        UUID citizenId,
        String citizenName,
        BigDecimal declaredWeight,
        BigDecimal verifiedWeight,
        String description,
        BigDecimal latitude,
        BigDecimal longitude,
        ReportStatus currentStatus,
        CancelReasonCode cancelReasonCode,
        LocalDateTime slaDeadlineAt,
        LocalDateTime createdAt
) {
}


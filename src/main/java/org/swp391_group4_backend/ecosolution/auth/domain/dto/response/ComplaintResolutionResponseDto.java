package org.swp391_group4_backend.ecosolution.auth.domain.dto.response;

import org.swp391_group4_backend.ecosolution.auth.domain.entity.ResolutionResult;

import java.time.LocalDateTime;
import java.util.UUID;

public record ComplaintResolutionResponseDto(
        UUID id,
        UUID complaintId,
        UUID adminId,
        String adminName,
        ResolutionResult result,
        String note,
        LocalDateTime createdAt
) {
}


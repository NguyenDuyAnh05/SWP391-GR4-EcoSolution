package org.swp391_group4_backend.ecosolution.reports.domain.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

public record WasteReportImageResponseDto(
        UUID id,
        UUID reportId,
        String imageUrl,
        LocalDateTime createdAt
) {
}



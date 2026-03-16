package org.swp391_group4_backend.ecosolution.dto.response;

import org.swp391_group4_backend.ecosolution.constant.ReportStatus;
import org.swp391_group4_backend.ecosolution.constant.WasteType;

import java.time.LocalDateTime;

public record ReportResponse(
        Long id,
        String description,
        String locationAddress,
        String imageUrl,
        WasteType wasteType,
        ReportStatus status,
        LocalDateTime createdAt,
        String citizenName,
        String collectorName
) {
}

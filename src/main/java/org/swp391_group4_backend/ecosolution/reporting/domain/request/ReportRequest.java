package org.swp391_group4_backend.ecosolution.reporting.domain.request;

import org.springframework.web.multipart.MultipartFile;

/**
 * Immutable DTO representing a citizen report submission from the UI.
 */
public record ReportRequest(
        String address,
        Long wardId,
        Double quantity,
        String description,
        org.swp391_group4_backend.ecosolution.reporting.domain.WasteType wasteType,
        MultipartFile image
) {}


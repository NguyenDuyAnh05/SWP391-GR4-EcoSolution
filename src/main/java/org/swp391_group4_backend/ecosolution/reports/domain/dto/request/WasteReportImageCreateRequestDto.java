package org.swp391_group4_backend.ecosolution.reports.domain.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record WasteReportImageCreateRequestDto(
    @NotNull(message = "Report ID is required")
    UUID reportId,

    @NotBlank(message = "Image URL is required")
    String imageUrl
) {
}


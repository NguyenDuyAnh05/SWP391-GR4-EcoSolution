package org.swp391_group4_backend.ecosolution.complaints.domain.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.swp391_group4_backend.ecosolution.complaints.domain.entity.ComplaintType;

import java.util.UUID;

public record ComplaintCreateRequestDto(
        @NotNull(message = "Report ID is required")
        UUID reportId,

        @NotNull(message = "Complaint type is required")
        ComplaintType type,

        @NotBlank(message = "Description is required")
        String description
) {
}




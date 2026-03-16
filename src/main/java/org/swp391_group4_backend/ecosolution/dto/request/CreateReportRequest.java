package org.swp391_group4_backend.ecosolution.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.swp391_group4_backend.ecosolution.constant.WasteType;

public record CreateReportRequest(
        @NotBlank(message = "Description is required")
        @Size(max = 500, message = "Description must be under 500 characters")
        String description,

        @NotBlank(message = "Image URL is required")
        String imageUrl,

        @NotNull(message = "Waste type must be selected")
        WasteType wasteType,

        @NotNull(message = "Citizen ID is required")
        Long citizenId
) {
}

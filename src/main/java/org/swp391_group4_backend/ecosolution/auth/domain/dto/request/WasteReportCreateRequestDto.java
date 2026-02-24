package org.swp391_group4_backend.ecosolution.auth.domain.dto.request;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record WasteReportCreateRequestDto(
        @NotNull(message = "Declared weight is required")
        @DecimalMin(value = "0.01", message = "Declared weight must be greater than 0")
        @DecimalMax(value = "10000.00", message = "Declared weight cannot exceed 10000 kg")
        BigDecimal declaredWeight,

        @NotBlank(message = "Description is required")
        @Size(min = 10, max = 5000, message = "Description must be between 10 and 5000 characters")
        String description,

        @NotNull(message = "Latitude is required")
        @DecimalMin(value = "-90.0", message = "Latitude must be between -90 and 90")
        @DecimalMax(value = "90.0", message = "Latitude must be between -90 and 90")
        BigDecimal latitude,

        @NotNull(message = "Longitude is required")
        @DecimalMin(value = "-180.0", message = "Longitude must be between -180 and 180")
        @DecimalMax(value = "180.0", message = "Longitude must be between -180 and 180")
        BigDecimal longitude
) {
}


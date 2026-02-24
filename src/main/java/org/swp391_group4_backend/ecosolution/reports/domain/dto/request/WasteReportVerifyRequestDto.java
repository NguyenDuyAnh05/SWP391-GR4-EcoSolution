package org.swp391_group4_backend.ecosolution.reports.domain.dto.request;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record WasteReportVerifyRequestDto(
        @NotNull(message = "Verified weight is required")
        @DecimalMin(value = "0.01", message = "Verified weight must be greater than 0")
        @DecimalMax(value = "10000.00", message = "Verified weight cannot exceed 10000 kg")
        BigDecimal verifiedWeight
) {
}


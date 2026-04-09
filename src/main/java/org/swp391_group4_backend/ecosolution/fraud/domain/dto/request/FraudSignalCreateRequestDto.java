package org.swp391_group4_backend.ecosolution.fraud.domain.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.swp391_group4_backend.ecosolution.fraud.domain.entity.FraudType;

import java.util.UUID;

public record FraudSignalCreateRequestDto(
        @NotNull(message = "Citizen ID is required")
        UUID citizenId,

        @NotNull(message = "Report ID is required")
        UUID reportId,

        @NotNull(message = "Fraud type is required")
        FraudType type,

        @NotNull(message = "Score is required")
        @Min(value = 0, message = "Score must be at least 0")
        @Max(value = 100, message = "Score must be at most 100")
        Integer score
) {
}


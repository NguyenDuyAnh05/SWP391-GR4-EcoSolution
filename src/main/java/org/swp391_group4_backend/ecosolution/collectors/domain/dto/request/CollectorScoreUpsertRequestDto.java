package org.swp391_group4_backend.ecosolution.collectors.domain.dto.request;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record CollectorScoreUpsertRequestDto(
    @NotNull(message = "Response rate is required")
    @DecimalMin(value = "0.00", message = "Response rate must be >= 0")
    @DecimalMax(value = "100.00", message = "Response rate must be <= 100")
    BigDecimal responseRate,

    @NotNull(message = "Completion rate is required")
    @DecimalMin(value = "0.00", message = "Completion rate must be >= 0")
    @DecimalMax(value = "100.00", message = "Completion rate must be <= 100")
    BigDecimal completionRate,

    @NotNull(message = "Complaint rate is required")
    @DecimalMin(value = "0.00", message = "Complaint rate must be >= 0")
    @DecimalMax(value = "100.00", message = "Complaint rate must be <= 100")
    BigDecimal complaintRate,

    @NotNull(message = "Reliability score is required")
    @DecimalMin(value = "0.00", message = "Reliability score must be >= 0")
    @DecimalMax(value = "100.00", message = "Reliability score must be <= 100")
    BigDecimal reliabilityScore
) {
}

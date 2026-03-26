package org.swp391_group4_backend.ecosolution.dto.request;

import jakarta.validation.constraints.NotNull;

public record VerifyReportRequest(
        @NotNull Long reportId,
        @NotNull Double actualWeight,
        @NotNull Long staffId // ID of staff at hub
) {
}

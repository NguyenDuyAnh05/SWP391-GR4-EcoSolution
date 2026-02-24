package org.swp391_group4_backend.ecosolution.auth.domain.dto.request;

import jakarta.validation.constraints.NotNull;
import org.swp391_group4_backend.ecosolution.auth.domain.entity.ResolutionResult;

import java.util.UUID;

public record ComplaintResolutionRequestDto(
        @NotNull(message = "Complaint ID is required")
        UUID complaintId,

        @NotNull(message = "Resolution result is required")
        ResolutionResult result,

        String note
) {
}


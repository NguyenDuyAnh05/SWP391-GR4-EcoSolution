package org.swp391_group4_backend.ecosolution.collectors.domain.dto.request;

import jakarta.validation.constraints.NotNull;
import org.swp391_group4_backend.ecosolution.tasks.domain.entity.TaskStatus;

public record CollectorStatusHistoryCreateRequestDto(
    @NotNull(message = "Status from is required")
    TaskStatus statusFrom,

    @NotNull(message = "Status to is required")
    TaskStatus statusTo,

    String reason
) {
}

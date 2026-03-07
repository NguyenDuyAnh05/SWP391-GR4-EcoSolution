package org.swp391_group4_backend.ecosolution.tasks.domain.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import org.swp391_group4_backend.ecosolution.tasks.domain.entity.TaskStatus;

@Schema(name = "CollectingTaskStatusUpdateRequest", description = "Payload used to update a task status")
public record CollectingTaskStatusUpdateRequestDto(
    @Schema(description = "Target status", example = "IN_PROGRESS")
    @NotNull(message = "Task status is required")
    TaskStatus status
) {
}

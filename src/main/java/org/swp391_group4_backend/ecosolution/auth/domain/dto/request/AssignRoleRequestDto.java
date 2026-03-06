package org.swp391_group4_backend.ecosolution.auth.domain.dto.request;

import jakarta.validation.constraints.NotNull;
import org.swp391_group4_backend.ecosolution.auth.domain.entity.UserRole;

import java.util.UUID;

public record AssignRoleRequestDto(
    @NotNull(message = "User ID is required")
    UUID userId,

    @NotNull(message = "Role is required")
    UserRole role
) {
}

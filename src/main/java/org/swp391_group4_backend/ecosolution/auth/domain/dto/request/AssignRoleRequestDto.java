package org.swp391_group4_backend.ecosolution.auth.domain.dto.request;

import jakarta.validation.constraints.NotNull;
import org.swp391_group4_backend.ecosolution.auth.domain.entity.UserRole;

public record AssignRoleRequestDto(
    @NotNull(message = "Role must not be null")
    UserRole role
) {
}


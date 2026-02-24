package org.swp391_group4_backend.ecosolution.auth.domain.dto.response;

import jakarta.validation.constraints.NotBlank;
import org.swp391_group4_backend.ecosolution.auth.domain.entity.UserRole;
import org.swp391_group4_backend.ecosolution.auth.domain.entity.UserStatus;

public record UserCreationResponseDto(
        String name,
        UserRole role,
        UserStatus status
) {

}

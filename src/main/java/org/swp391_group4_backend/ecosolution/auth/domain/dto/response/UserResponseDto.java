package org.swp391_group4_backend.ecosolution.auth.domain.dto.response;

import org.swp391_group4_backend.ecosolution.auth.domain.entity.UserRole;
import org.swp391_group4_backend.ecosolution.auth.domain.entity.UserStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record UserResponseDto(
    UUID id,
    String name,
    String email,
    UserRole role,
    UserStatus status,
    LocalDateTime createdAt
) {
}


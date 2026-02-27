package org.swp391_group4_backend.ecosolution.auth.domain.dto.response;

import org.swp391_group4_backend.ecosolution.auth.domain.entity.UserRole;
import org.swp391_group4_backend.ecosolution.auth.domain.entity.UserStatus;

import java.util.UUID;

public record AuthTokenResponseDto(
    String accessToken,
    String tokenType,
    long expiresInSeconds,
    UUID userId,
    UserRole role,
    UserStatus status,
    String name,
    String email
) {
}


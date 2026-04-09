package org.swp391_group4_backend.ecosolution.auth.domain.dto.response;

import org.swp391_group4_backend.ecosolution.auth.domain.entity.UserRole;

import java.util.UUID;

public record LoginResponseDto(
    String tokenType,
    String accessToken,
    long expiresInSeconds,
    UUID userId,
    UserRole role
) {
}


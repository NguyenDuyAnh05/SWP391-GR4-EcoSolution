package org.swp391_group4_backend.ecosolution.auth.domain.dto.request;

public record AccountCreationRequestDto(
        String username,
        String password,
        String name
) {
}

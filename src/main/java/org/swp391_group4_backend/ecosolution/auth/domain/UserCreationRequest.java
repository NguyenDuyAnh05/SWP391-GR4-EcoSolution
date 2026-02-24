package org.swp391_group4_backend.ecosolution.auth.domain;

public record UserCreationRequest(
        String email,
        String password,
        String name
) {
}

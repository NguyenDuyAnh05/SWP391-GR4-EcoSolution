package org.swp391_group4_backend.ecosolution.auth.domain;

public record AccountCreationRequest(
        String username,
        String password,
        String name
) {
}

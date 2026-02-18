package org.swp391_group4_backend.ecosolution.auth.domain.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record AccountCreationRequestDto(
        @NotBlank(message = "Username must not be blank")
        @NotNull
        String username,

        @NotNull
        @Size(min=8, message = "Password must be at least 8 characters long")
        String password,

        @NotNull
        @NotBlank(message = "Name must not be blank")
        String name
) {
}

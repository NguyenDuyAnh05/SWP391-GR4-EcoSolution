package org.swp391_group4_backend.ecosolution.auth.domain.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record AuthLoginRequestDto(
    @NotNull
    @NotBlank(message = "Username must not be blank")
    String username,

    @NotNull
    @Size(min = 8, message = "Password must be at least 8 characters long")
    String password
) {
}


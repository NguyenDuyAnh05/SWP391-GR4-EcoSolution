package org.swp391_group4_backend.ecosolution.auth.domain.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UserCreationRequestDto(
        @NotBlank(message = "Email must not be blank")
        @NotNull
        @Email(message = "Email must be valid")
        String email,

        @NotNull
        @Size(min=8, message = "Password must be at least 8 characters long")
        String password,

        @NotNull
        @NotBlank(message = "Name must not be blank")
        String name
) {
}

package org.swp391_group4_backend.ecosolution.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.swp391_group4_backend.ecosolution.constant.UserRole;

public record RegisterRequest(
    @NotBlank(message = "Username is required") String username,
    @NotBlank(message = "Password is required") String password,
    @NotBlank(message = "First name is required") String firstName,
    @NotBlank(message = "Last name is required") String lastName
) {}

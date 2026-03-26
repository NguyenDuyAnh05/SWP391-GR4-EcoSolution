package org.swp391_group4_backend.ecosolution.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.swp391_group4_backend.ecosolution.constant.UserRole;
import org.swp391_group4_backend.ecosolution.entity.Ward;

public record RegisterRequest(
        @NotBlank(message = "Username is required")
        @Size(min = 4, max = 20, message = "Username must be between 4 and 20 characters")
        String username,

        @NotBlank(message = "Password is required")
        @Size(min = 6, message = "Password must be at least 6 characters")
        String password,

        @NotBlank(message = "First name is required")
        String firstName,

        @NotBlank(message = "Last name is required")
        String lastName,

        @NotBlank(message = "Phone is required")
        @Pattern(regexp = "^(0|\\+84)[3|5|7|8|9][0-9]{8}$", message = "Invalid Vietnamese phone number format")
        String phone
        ) {}

package org.swp391_group4_backend.ecosolution.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ActivationRequest(
        @NotNull(message = "User ID is required") Long userId,
        @NotNull(message = "Ward ID is required") Long wardId,
        @NotNull(message = "Subscription Tier ID is required") Long tierId,
        @NotBlank(message = "Address is required") String address
) {
}

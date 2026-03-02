package org.swp391_group4_backend.ecosolution.wasterequest.domain.dto.request;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record WasteRequestAssignRequestDto(
    @NotNull(message = "Collector ID is mandatory")
    UUID collectorId
) {}


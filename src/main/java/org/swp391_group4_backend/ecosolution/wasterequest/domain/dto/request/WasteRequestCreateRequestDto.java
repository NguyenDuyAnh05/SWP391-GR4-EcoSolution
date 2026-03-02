package org.swp391_group4_backend.ecosolution.wasterequest.domain.dto.request;

import jakarta.validation.constraints.*;
import org.swp391_group4_backend.ecosolution.wasterequest.domain.entity.WasteType;

import java.math.BigDecimal;
import java.time.LocalDate;

public record WasteRequestCreateRequestDto(
    @NotNull(message = "Waste type is mandatory")
    WasteType wasteType,

    @NotNull(message = "Quantity is mandatory")
    @DecimalMin(value = "0.01", message = "Quantity must be greater than 0")
    @Digits(integer = 8, fraction = 2, message = "Quantity must be rounded to 2 decimal places")
    BigDecimal quantity,

    @NotBlank(message = "Address is mandatory")
    String address,

    @NotNull(message = "Latitude is mandatory")
    @Min(value = -90, message = "Latitude must be between -90 and 90")
    @Max(value = 90, message = "Latitude must be between -90 and 90")
    Double latitude,

    @NotNull(message = "Longitude is mandatory")
    @Min(value = -180, message = "Longitude must be between -180 and 180")
    @Max(value = 180, message = "Longitude must be between -180 and 180")
    Double longitude,

    @NotNull(message = "Preferred date is mandatory")
    @FutureOrPresent(message = "Preferred date must not be in the past")
    LocalDate preferredDate
) {}

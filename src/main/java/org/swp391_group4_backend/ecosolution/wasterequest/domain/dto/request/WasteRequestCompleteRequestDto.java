package org.swp391_group4_backend.ecosolution.wasterequest.domain.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import org.swp391_group4_backend.ecosolution.wasterequest.domain.entity.WasteType;

import java.math.BigDecimal;

/**
 * BR19: Completion requires evidence image (sent as multipart, not in this body).
 * BR18: Collector may optionally adjust waste type and quantity.
 *       If not provided, actual values auto-copy from citizen's original.
 */
public record WasteRequestCompleteRequestDto(
    WasteType actualWasteType,

    @DecimalMin(value = "0.01", message = "Actual quantity must be greater than 0")
    @Digits(integer = 8, fraction = 2, message = "Actual quantity must be rounded to 2 decimal places")
    BigDecimal actualQuantity
) {}


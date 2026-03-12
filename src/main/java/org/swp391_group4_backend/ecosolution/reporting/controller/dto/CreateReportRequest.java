package org.swp391_group4_backend.ecosolution.reporting.controller.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * TODO NN: Controller-layer DTO (record) for report creation form binding.
 * - Uses Java Record for immutability and concise syntax.
 * - This object is specific to the web layer (Thymeleaf form binding).
 */
public record CreateReportRequest(
        String wasteType,
        String address,
        Long wardId,
        LocalDate preferredDate,
        BigDecimal submittedQuantity,
        String description
) {
}


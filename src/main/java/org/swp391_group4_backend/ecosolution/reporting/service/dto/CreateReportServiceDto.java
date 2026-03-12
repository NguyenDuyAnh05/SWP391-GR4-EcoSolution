package org.swp391_group4_backend.ecosolution.reporting.service.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import org.swp391_group4_backend.ecosolution.reporting.domain.WasteType;

/** Service-layer DTO (record) */
public record CreateReportServiceDto(
        WasteType wasteType,
        String address,
        Long wardId,
        LocalDate preferredDate,
        BigDecimal submittedQuantity,
        String description,
        byte[] image
) {}


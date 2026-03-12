package org.swp391_group4_backend.ecosolution.reporting.service.dto;

import org.swp391_group4_backend.ecosolution.reporting.domain.WasteType;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * TODO 12: DTO for creating a WasteReport (used by service layer tests)
 */
public class CreateReportDto {
    public WasteType wasteType;
    public String address;
    public Long wardId;
    public LocalDate preferredDate;
    public BigDecimal submittedQuantity;
    public String description;
    public byte[] image;
}


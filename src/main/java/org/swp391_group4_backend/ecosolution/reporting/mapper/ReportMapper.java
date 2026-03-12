package org.swp391_group4_backend.ecosolution.reporting.mapper;

import org.swp391_group4_backend.ecosolution.reporting.controller.dto.CreateReportRequest;
import org.swp391_group4_backend.ecosolution.reporting.service.dto.CreateReportServiceDto;
import org.swp391_group4_backend.ecosolution.reporting.domain.WasteType;

public interface ReportMapper {
    CreateReportServiceDto toServiceDto(CreateReportRequest req, byte[] imageBytes);

    static WasteType toWasteType(String s) {
        if (s == null) return null;
        return WasteType.valueOf(s.toUpperCase());
    }
}


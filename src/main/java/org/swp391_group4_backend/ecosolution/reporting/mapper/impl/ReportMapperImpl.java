package org.swp391_group4_backend.ecosolution.reporting.mapper.impl;

import org.springframework.stereotype.Component;
import org.swp391_group4_backend.ecosolution.reporting.controller.dto.CreateReportRequest;
import org.swp391_group4_backend.ecosolution.reporting.mapper.ReportMapper;
import org.swp391_group4_backend.ecosolution.reporting.service.dto.CreateReportServiceDto;

@Component
public class ReportMapperImpl implements ReportMapper {

    @Override
    public CreateReportServiceDto toServiceDto(CreateReportRequest req, byte[] imageBytes) {
        return new CreateReportServiceDto(
                ReportMapper.toWasteType(req.wasteType()),
                req.address(),
                req.wardId(),
                req.preferredDate(),
                req.submittedQuantity(),
                req.description(),
                imageBytes
        );
    }
}


package org.swp391_group4_backend.ecosolution.mapper.impl;

import org.springframework.stereotype.Component;
import org.swp391_group4_backend.ecosolution.dto.request.CreateReportRequest;
import org.swp391_group4_backend.ecosolution.entity.WasteReport;
import org.swp391_group4_backend.ecosolution.mapper.WasteReportMapper;

@Component
public class WasteReportMapperImpl implements WasteReportMapper {
    @Override
    public WasteReport toEntity(CreateReportRequest request) {
        WasteReport wasteReport = new WasteReport();
        wasteReport.setDescription(request.description());
        wasteReport.setImageUrl(request.imageUrl());
        wasteReport.setWasteType(request.wasteType());

        return wasteReport;
    }
}
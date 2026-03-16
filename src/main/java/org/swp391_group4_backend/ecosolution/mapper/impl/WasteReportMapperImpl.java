package org.swp391_group4_backend.ecosolution.mapper.impl;

import org.springframework.stereotype.Component;
import org.swp391_group4_backend.ecosolution.dto.request.CreateReportRequest;
import org.swp391_group4_backend.ecosolution.dto.response.ReportResponse;
import org.swp391_group4_backend.ecosolution.entity.WasteReport;
import org.swp391_group4_backend.ecosolution.mapper.WasteReportMapper;

@Component
public class WasteReportMapperImpl implements WasteReportMapper {
    // 1. Dùng ở CONTROLLER: Biến Entity thành DTO để trả về cho Client
    @Override
    public ReportResponse toDto(WasteReport entity) {
        if (entity == null) return null;

        return new ReportResponse(
                entity.getId(),
                entity.getDescription(),
                entity.getImageUrl(),
                entity.getWasteType(),
                entity.getReportStatus(),
                entity.getCreatedAt(),
                entity.getCitizen().getFirstName() + " " + entity.getCitizen().getLastName(),
                entity.getCollector() != null ? entity.getCollector().getUsername() : "Unassigned"
        );
    }

    // 2. Dùng ở SERVICE: Biến Request của Citizen thành Entity để lưu vào DB
    @Override
    public WasteReport toEntity(CreateReportRequest request) {
        if (request == null) return null;

        WasteReport wasteReport = new WasteReport();
        wasteReport.setDescription(request.description());
        wasteReport.setImageUrl(request.imageUrl());
        wasteReport.setWasteType(request.wasteType());
        // Lưu ý: reportStatus và createdAt đã có @PrePersist lo trong Entity rồi

        return wasteReport;
    }

    // Hàm này từ BaseMapper nhưng chúng ta không dùng chiều ngược lại của Response
    @Override
    public WasteReport toEntity(ReportResponse dto) {
        return null;
    }
}
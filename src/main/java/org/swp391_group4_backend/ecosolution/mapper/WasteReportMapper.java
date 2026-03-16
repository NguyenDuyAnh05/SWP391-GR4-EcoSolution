package org.swp391_group4_backend.ecosolution.mapper;

import org.swp391_group4_backend.ecosolution.dto.request.CreateReportRequest;
import org.swp391_group4_backend.ecosolution.dto.response.ReportResponse;
import org.swp391_group4_backend.ecosolution.entity.WasteReport;
public interface WasteReportMapper extends BaseMapper<ReportResponse, WasteReport> {

    WasteReport toEntity(CreateReportRequest request);
}

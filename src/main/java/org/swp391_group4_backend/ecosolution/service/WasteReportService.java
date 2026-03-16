package org.swp391_group4_backend.ecosolution.service;

import org.swp391_group4_backend.ecosolution.dto.request.AssignCollectorRequest;
import org.swp391_group4_backend.ecosolution.dto.request.CreateReportRequest;
import org.swp391_group4_backend.ecosolution.dto.request.UpdateStatusRequest;
import org.swp391_group4_backend.ecosolution.entity.WasteReport;

import java.util.List;
import java.util.Optional;

public interface WasteReportService {
    WasteReport createReport(CreateReportRequest request);
    List<WasteReport> getReportsByCitizen(Long citizenId);

    // For Manager
    WasteReport assignCollector(AssignCollectorRequest request);
    List<WasteReport> getPendingReports();

    // For Collector
    WasteReport updateStatus(Long reportId, UpdateStatusRequest request);
    List<WasteReport> getTasksForCollector(Long collectorId);
}


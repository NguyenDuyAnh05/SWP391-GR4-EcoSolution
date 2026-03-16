package org.swp391_group4_backend.ecosolution.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.swp391_group4_backend.ecosolution.dto.request.AssignCollectorRequest;
import org.swp391_group4_backend.ecosolution.dto.request.CreateReportRequest;
import org.swp391_group4_backend.ecosolution.dto.request.UpdateStatusRequest;
import org.swp391_group4_backend.ecosolution.dto.response.ReportResponse;
import org.swp391_group4_backend.ecosolution.entity.WasteReport;
import org.swp391_group4_backend.ecosolution.mapper.WasteReportMapper;
import org.swp391_group4_backend.ecosolution.service.WasteReportService;

import java.util.List;

@RequestMapping("/api/v1/reports")
public class WasteReportController {
    private final WasteReportService reportService;
    private final WasteReportMapper reportMapper;

    public WasteReportController(WasteReportService reportService, WasteReportMapper reportMapper) {
        this.reportService = reportService;
        this.reportMapper = reportMapper;
    }
    @PostMapping
    public ResponseEntity<ReportResponse> createReport(@Valid @RequestBody CreateReportRequest request) {
        WasteReport createdReport = reportService.createReport(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(reportMapper.toDto(createdReport));
    }

    @GetMapping("/citizen/{citizenId}")
    public ResponseEntity<List<ReportResponse>> getCitizenHistory(@PathVariable Long citizenId) {
        List<WasteReport> reports = reportService.getReportsByCitizen(citizenId);
        return ResponseEntity.ok(reportMapper.toDtoList(reports));
    }

    // --- MANAGER: View pending and assign tasks ---
    @GetMapping("/pending")
    public ResponseEntity<List<ReportResponse>> getPendingReports() {
        List<WasteReport> reports = reportService.getPendingReports();
        return ResponseEntity.ok(reportMapper.toDtoList(reports));
    }

    @PutMapping("/assign")
    public ResponseEntity<ReportResponse> assignCollector(@Valid @RequestBody AssignCollectorRequest request) {
        WasteReport assignedReport = reportService.assignCollector(request);
        return ResponseEntity.ok(reportMapper.toDto(assignedReport));
    }

    // --- COLLECTOR: View tasks and update status ---
    @GetMapping("/collector/{collectorId}")
    public ResponseEntity<List<ReportResponse>> getCollectorTasks(@PathVariable Long collectorId) {
        List<WasteReport> reports = reportService.getTasksForCollector(collectorId);
        return ResponseEntity.ok(reportMapper.toDtoList(reports));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<ReportResponse> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateStatusRequest request) {
        WasteReport updatedReport = reportService.updateStatus(id, request);
        return ResponseEntity.ok(reportMapper.toDto(updatedReport));
    }
}

package org.swp391_group4_backend.ecosolution.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.swp391_group4_backend.ecosolution.dto.request.TrashWeightInput;
import org.swp391_group4_backend.ecosolution.dto.response.TrashReportResponse;
import org.swp391_group4_backend.ecosolution.service.TrashReportService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/trash-reports")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class TrashReportController {

    private final TrashReportService trashReportService;

    @PostMapping("/citizen/{citizenId}")
    public ResponseEntity<TrashReportResponse> createPendingReport(@PathVariable Long citizenId) {
        return ResponseEntity.ok(trashReportService.createPendingReport(citizenId));
    }

    @GetMapping("/citizen/{citizenId}")
    public ResponseEntity<List<TrashReportResponse>> getCitizenReports(@PathVariable Long citizenId) {
        return ResponseEntity.ok(trashReportService.getCitizenReports(citizenId));
    }

    @GetMapping("/receiver/{receiverId}/pending")
    public ResponseEntity<List<TrashReportResponse>> getPendingReportsForReceiver(@PathVariable Long receiverId) {
        return ResponseEntity.ok(trashReportService.getPendingReportsForReceiver(receiverId));
    }

    @PutMapping("/{reportId}/confirm")
    public ResponseEntity<String> confirmReport(@PathVariable Long reportId, @RequestBody List<TrashWeightInput> weightsInput) {
        trashReportService.confirmAndCalculatePoints(reportId, weightsInput);
        return ResponseEntity.ok("Confirmed and calculated points successfully.");
    }
}

package org.swp391_group4_backend.ecosolution.service;

import org.swp391_group4_backend.ecosolution.dto.request.TrashWeightInput;

import org.swp391_group4_backend.ecosolution.dto.response.TrashReportResponse;

import java.util.List;

public interface TrashReportService {
    void confirmAndCalculatePoints(Long reportId, List<TrashWeightInput> weightsInput);
    TrashReportResponse createPendingReport(Long citizenId);
    List<TrashReportResponse> getCitizenReports(Long citizenId);
    List<TrashReportResponse> getPendingReportsForReceiver(Long receiverId);
}


package org.swp391_group4_backend.ecosolution.service.impl;

import org.springframework.stereotype.Service;
import org.swp391_group4_backend.ecosolution.constant.ReportStatus;
import org.swp391_group4_backend.ecosolution.dto.response.CollectorStatResponse;
import org.swp391_group4_backend.ecosolution.dto.response.StatsSummaryResponse;
import org.swp391_group4_backend.ecosolution.repository.WasteReportRepository;
import org.swp391_group4_backend.ecosolution.service.AdminService;

import java.util.List;

@Service
public class AdminServiceImpl implements AdminService {

    private final WasteReportRepository reportRepository;

    public AdminServiceImpl(WasteReportRepository reportRepository) {
        this.reportRepository = reportRepository;
    }

    @Override
    public StatsSummaryResponse getSummaryStats() {
        long pending = reportRepository.countByReportStatus(ReportStatus.PENDING);
        long assigned = reportRepository.countByReportStatus(ReportStatus.ASSIGNED);
        long inProgress = reportRepository.countByReportStatus(ReportStatus.IN_PROGRESS);
        long collected = reportRepository.countByReportStatus(ReportStatus.COLLECTED);

        return new StatsSummaryResponse(pending, assigned, inProgress, collected);
    }

    @Override
    public List<CollectorStatResponse> getTopCollectors() {
        return reportRepository.findTopCollectors();
    }
}

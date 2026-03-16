package org.swp391_group4_backend.ecosolution.service.impl;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.swp391_group4_backend.ecosolution.constant.ReportStatus;
import org.swp391_group4_backend.ecosolution.constant.UserRole;
import org.swp391_group4_backend.ecosolution.dto.request.AssignCollectorRequest;
import org.swp391_group4_backend.ecosolution.dto.request.CreateReportRequest;
import org.swp391_group4_backend.ecosolution.dto.request.UpdateStatusRequest;
import org.swp391_group4_backend.ecosolution.entity.User;
import org.swp391_group4_backend.ecosolution.entity.WasteReport;
import org.swp391_group4_backend.ecosolution.mapper.WasteReportMapper;
import org.swp391_group4_backend.ecosolution.repository.UserRepository;
import org.swp391_group4_backend.ecosolution.repository.WasteReportRepository;
import org.swp391_group4_backend.ecosolution.service.WasteReportService;

import java.util.List;

@Service
public class WasteReportServiceImpl implements WasteReportService {
    private final UserRepository userRepository;
    private final WasteReportRepository reportRepository;
    private final WasteReportMapper mapper;

    public WasteReportServiceImpl(UserRepository userRepository, WasteReportRepository reportRepository, WasteReportMapper mapper) {
        this.userRepository = userRepository;
        this.reportRepository = reportRepository;
        this.mapper = mapper;
    }

    @Override
    public WasteReport createReport(CreateReportRequest request) {
        Long citizenId = request.citizenId();
        User citizen = userRepository.findById(citizenId).orElseThrow(() -> new EntityNotFoundException("Citizen not found with id: " + citizenId));
        WasteReport report = mapper.toEntity(request);
        report.setCitizen(citizen);

        return reportRepository.save(report);
    }

    @Override
    public List<WasteReport> getReportsByCitizen(Long citizenId) {
        return reportRepository.findByCitizenId(citizenId);
    }

    @Override
    public WasteReport assignCollector(AssignCollectorRequest request) {
        WasteReport report = reportRepository.findById(request.reportId())
                .orElseThrow(() -> new EntityNotFoundException("Report not found with id: " + request.reportId()));

        User collector = userRepository.findById(request.collectorId())
                .orElseThrow(() -> new EntityNotFoundException("Collector not found with id: " + request.collectorId()));

        if (collector.getRole() != UserRole.COLLECTOR) {
            throw new IllegalArgumentException("User is not a COLLECTOR");
        }

        report.setCollector(collector);
        report.setReportStatus(ReportStatus.ASSIGNED);

        return reportRepository.save(report);
    }

    @Override
    public List<WasteReport> getPendingReports() {
        return reportRepository.findByReportStatus(ReportStatus.PENDING);
    }

    @Override
    public WasteReport updateStatus(Long reportId, UpdateStatusRequest request) {
        WasteReport report = reportRepository.findById(reportId)
                .orElseThrow(() -> new EntityNotFoundException("Report not found with id: " + reportId));

        report.setReportStatus(request.status());

        // Nếu đã thu gom xong và có ảnh minh chứng thì lưu lại
        if (request.status() == ReportStatus.COLLECTED && request.confirmationImageUrl() != null) {
            report.setImageUrl(request.confirmationImageUrl());
        }

        return reportRepository.save(report);
    }

    @Override
    public List<WasteReport> getTasksForCollector(Long collectorId) {
        return reportRepository.findByCollectorId(collectorId);
    }


}

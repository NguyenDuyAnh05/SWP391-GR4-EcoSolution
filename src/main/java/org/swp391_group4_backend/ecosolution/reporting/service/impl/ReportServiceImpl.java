package org.swp391_group4_backend.ecosolution.reporting.service.impl;

import org.swp391_group4_backend.ecosolution.core.service.UserService;
import org.swp391_group4_backend.ecosolution.core.repository.UserRepository;
import org.swp391_group4_backend.ecosolution.core.domain.entity.User;
import org.swp391_group4_backend.ecosolution.reporting.domain.ReportStatus;
import org.swp391_group4_backend.ecosolution.reporting.domain.entity.WasteReport;
import org.swp391_group4_backend.ecosolution.reporting.domain.request.ReportRequest;
import org.swp391_group4_backend.ecosolution.reporting.mapper.impl.ReportMapperImpl;
import org.swp391_group4_backend.ecosolution.reporting.repository.ReportRepository;
import org.swp391_group4_backend.ecosolution.reporting.service.ReportService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.OffsetDateTime;
import java.util.List;

@Service
@Transactional
public class ReportServiceImpl implements ReportService {
    private static final Logger log = LoggerFactory.getLogger(ReportServiceImpl.class);
    // BR-07 requires duplicate window: 5 minutes (300 seconds)
    private static final long DUPLICATE_WINDOW_SECONDS = 300L;

    private final ReportMapperImpl mapper;
    private final ReportRepository reportRepository;
    private final UserService userService;
    private final UserRepository userRepository;

    public ReportServiceImpl(ReportMapperImpl mapper, ReportRepository reportRepository, UserService userService, UserRepository userRepository) {
        this.mapper = mapper;
        this.reportRepository = reportRepository;
        this.userService = userService;
        this.userRepository = userRepository;
    }

    @Override
    public void claimReport(java.util.UUID reportId) {
        var user = userService.getCurrentUser();
        if (user == null || !"ENTERPRISE".equalsIgnoreCase(user.getRole())) {
            log.warn("Unauthorized claim attempt by user {}", user);
            throw new IllegalStateException("Only ENTERPRISE users can claim reports");
        }

        var opt = reportRepository.findById(reportId);
        if (opt.isEmpty()) throw new IllegalStateException("Report not found");
        var report = opt.get();
        if (report.getStatus() != ReportStatus.PENDING) {
            log.warn("Attempt to claim non-pending report {} status={}", reportId, report.getStatus());
            throw new IllegalStateException("Only PENDING reports can be claimed");
        }

        report.setStatus(ReportStatus.ACCEPTED);
        reportRepository.save(report);
        log.info("Report {} claimed by enterprise user {}", reportId, user.getId());
    }

    @Override
    public void assignCollector(java.util.UUID reportId, java.util.UUID collectorId) {
        var user = userService.getCurrentUser();
        if (user == null || !"BOSS".equalsIgnoreCase(user.getRole())) {
            log.warn("Unauthorized assign attempt by user {}", user);
            throw new IllegalStateException("Only BOSS users can assign collectors");
        }

        var opt = reportRepository.findById(reportId);
        if (opt.isEmpty()) throw new IllegalStateException("Report not found");
        var report = opt.get();
        if (report.getStatus() != ReportStatus.ACCEPTED) throw new IllegalStateException("Only ACCEPTED reports can be assigned");

        // Validate collector exists and works for this boss (employerId == boss.id)
        var collOpt = userRepository.findById(collectorId);
        if (collOpt.isEmpty()) throw new IllegalStateException("Collector not found");
        User collector = collOpt.get();
        if (collector.getEmployerId() == null || !collector.getEmployerId().equals(user.getId())) {
            log.warn("Collector {} does not belong to boss {}", collectorId, user.getId());
            throw new IllegalStateException("Collector does not work for this boss");
        }

        report.setAssignedTo(collectorId);
        report.setAssignedBy(user.getId());
        report.setAssignedAt(OffsetDateTime.now());
        report.setStatus(ReportStatus.ASSIGNED);
        reportRepository.save(report);
        log.info("Report {} assigned to collector {} by boss {}", reportId, collectorId, user.getId());
    }

    @Override
    public void completeCollection(org.swp391_group4_backend.ecosolution.reporting.domain.request.CollectionCompleteRequest req) {
        var user = userService.getCurrentUser();
        if (user == null) throw new IllegalStateException("User not authenticated");

        var opt = reportRepository.findById(req.reportId());
        if (opt.isEmpty()) throw new IllegalStateException("Report not found");
        var report = opt.get();
        if (report.getStatus() != ReportStatus.ASSIGNED) {
            log.warn("Attempt to complete non-assigned report {} status={}", req.reportId(), report.getStatus());
            throw new IllegalStateException("Only ASSIGNED reports can be completed");
        }

        // Ensure current user is the assigned collector
        if (report.getAssignedTo() == null || !report.getAssignedTo().equals(user.getId())) {
            throw new IllegalStateException("Only the assigned collector can complete this report");
        }

        var proof = req.proofImage();
        if (proof == null || proof.isEmpty()) throw new IllegalArgumentException("Proof image is required");
        if (proof.getSize() > ReportMapperImpl.MAX_IMAGE_BYTES) throw new IllegalArgumentException("Proof image exceeds maximum allowed size of 5MB");
        try {
            report.setProofImage(proof.getBytes());
        } catch (java.io.IOException e) {
            log.error("Failed to read proof image for report {}", req.reportId(), e);
            throw new RuntimeException("Failed to read proof image", e);
        }

        report.setActualQuantity(req.actualQuantity());
        report.setStatus(ReportStatus.COLLECTED);
        reportRepository.save(report);
        log.info("Report {} marked COLLECTED by collector {}", req.reportId(), user.getId());

        // Award points to the report owner: points = round(actualQuantity * 10)
        var owner = report.getCreatedBy();
        if (owner != null && owner.getId() != null) {
            var ownerOpt = userRepository.findById(owner.getId());
            if (ownerOpt.isPresent()) {
                User ownerEntity = ownerOpt.get();
                int add = (int) Math.round((req.actualQuantity() != null ? req.actualQuantity() : 0.0) * 10.0);
                Integer current = ownerEntity.getPoints();
                if (current == null) current = 0;
                ownerEntity.setPoints(current + add);
                userRepository.save(ownerEntity);
                log.info("Awarded {} points to user {} for report {}", add, ownerEntity.getId(), req.reportId());
            }
        }
    }

    @Override
    public WasteReport createReport(ReportRequest request) {
        try {
            WasteReport entity = mapper.toEntity(request);

            // populate createdBy
            User user = userService.getCurrentUser();
            entity.setCreatedBy(user);

            // duplicate detection (same ward + address by same user within window)
            if (user != null && user.getId() != null) {
                Long wardId = entity.getWard() != null ? entity.getWard().getId() : null;
                OffsetDateTime cutoff = OffsetDateTime.now().minusSeconds(DUPLICATE_WINDOW_SECONDS);
                List<WasteReport> matches = reportRepository.findRecentByCreatedByAndAddressAndWard(user.getId(),
                        normalizeAddress(entity.getAddress()),
                        wardId,
                        cutoff);

                boolean recentDuplicate = matches.stream()
                        .anyMatch(r -> r.getStatus() != null && r.getStatus() != ReportStatus.CANCELLED);

                        if (recentDuplicate) {
                            log.warn("Duplicate report detected for user {} address={} ward={}", user.getId(), entity.getAddress(), wardId);
                            throw new IllegalStateException("A report for this location was already recently submitted.");
                        }
            }

            entity.setStatus(ReportStatus.PENDING);
            return reportRepository.save(entity);
        } catch (IllegalArgumentException iae) {
            throw iae;
        } catch (Exception ex) {
            throw new RuntimeException("Failed to create report", ex);
        }
    }

    @Override
    public java.util.List<WasteReport> getReportsForCurrentUser() {
        var user = userService.getCurrentUser();
        if (user == null || user.getId() == null) return java.util.Collections.emptyList();
        return reportRepository.findAllByCreatedByIdOrderByCreatedAtDesc(user.getId());
    }

    @Override
    public java.util.List<WasteReport> findPendingReports() {
        return reportRepository.findAllByStatusOrderByCreatedAtDesc(ReportStatus.PENDING);
    }

    @Override
    public java.util.List<WasteReport> findAcceptedReports() {
        return reportRepository.findAllByStatusOrderByCreatedAtDesc(ReportStatus.ACCEPTED);
    }

    @Override
    public java.util.List<WasteReport> findAssignedReports() {
        return reportRepository.findAllByStatusOrderByCreatedAtDesc(ReportStatus.ASSIGNED);
    }

    @Override
    public java.util.List<WasteReport> findAssignedReportsForCurrentUser() {
        var user = userService.getCurrentUser();
        if (user == null || user.getId() == null) return java.util.Collections.emptyList();
        return reportRepository.findAllByAssignedToOrderByCreatedAtDesc(user.getId());
    }

    @Override
    public void cancelReport(java.util.UUID id) {
        var user = userService.getCurrentUser();
        if (user == null || user.getId() == null) throw new IllegalStateException("User not authenticated");

        var opt = reportRepository.findById(id);
        if (opt.isEmpty()) throw new IllegalStateException("Report not found");
        var report = opt.get();
        if (!user.getId().equals(report.getCreatedBy().getId())) throw new IllegalStateException("Not authorized to cancel this report");
        if (report.getStatus() != ReportStatus.PENDING) throw new IllegalStateException("Only PENDING reports can be cancelled");

        report.setStatus(ReportStatus.CANCELLED);
        reportRepository.save(report);
    }

    private String normalizeAddress(String addr) {
        if (addr == null) return null;
        return addr.trim().toLowerCase();
    }
}




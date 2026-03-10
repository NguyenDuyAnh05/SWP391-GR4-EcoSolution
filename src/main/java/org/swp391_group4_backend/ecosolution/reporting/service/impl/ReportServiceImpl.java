package org.swp391_group4_backend.ecosolution.reporting.service.impl;


import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.swp391_group4_backend.ecosolution.core.domain.UserRole;
import org.swp391_group4_backend.ecosolution.core.domain.entity.User;
import org.swp391_group4_backend.ecosolution.core.repository.UserRepository;
import org.swp391_group4_backend.ecosolution.reporting.domain.ReportStatus;
import org.swp391_group4_backend.ecosolution.reporting.domain.entity.WasteReport;
import org.swp391_group4_backend.ecosolution.reporting.repository.WasteReportRepository;
import org.swp391_group4_backend.ecosolution.reporting.service.ReportService;

/**
 * TODO 05: Implement ReportServiceImpl.
 * <p>
 * - Enforces the state machine: PENDING → ACCEPTED → ASSIGNED → COLLECTED (BR-01).
 * - Enforces proof image requirement before COLLECTED (BR-04).
 * - This is where ALL business logic for transitions lives.
 * <p>
 * Annotations needed: @Service, @RequiredArgsConstructor (or manual constructor)
 * Inject: WasteReportRepository
 */
@Service
public class ReportServiceImpl implements ReportService {


    // TODO 05a: Define the valid transitions map.
    // Use a static Map<ReportStatus, ReportStatus> where:
    //   key = current status, value = only allowed next status.
    //   PENDING   → ACCEPTED
    //   ACCEPTED  → ASSIGNED
    //   ASSIGNED  → COLLECTED
    // COLLECTED is not a key (terminal state — no transitions out).
    //
    // Example: private static final Map<ReportStatus, ReportStatus> VALID_TRANSITIONS = Map.of(
    //     ReportStatus.PENDING, ReportStatus.ACCEPTED,
    //     ...
    // );
    private static Map<ReportStatus, ReportStatus> VALID_TRANSITIONS = Map.of(
            ReportStatus.PENDING, ReportStatus.ACCEPTED,
            ReportStatus.ACCEPTED, ReportStatus.ASSIGNED,
            ReportStatus.ASSIGNED, ReportStatus.COLLECTED); //Collected is the terminal state, so it is not a key in the map.

    // TODO 05b: Inject WasteReportRepository via constructor.
    private final WasteReportRepository reportRepository;
    // TODO 10a: Inject UserRepository (needed for claim/assign to look up users)
    private final UserRepository userRepository;

    public ReportServiceImpl(WasteReportRepository reportRepository, UserRepository userRepository) {
        this.reportRepository = reportRepository;
        this.userRepository = userRepository;
    }
    // TODO 05c: Implement transitionStatus(UUID reportId, ReportStatus newStatus)
    //
    //   Step 1: Find report by ID. Throw if not found.
    //
    //   Step 2: Get current status from the report.
    //           Look up allowed next status from VALID_TRANSITIONS map.
    //           If current status is NOT in the map → terminal state → throw IllegalStateException.
    //           If newStatus != allowed next → invalid transition → throw IllegalStateException.
    //
    //   Step 3 (BR-04): If newStatus == COLLECTED, check report.getProofImagePath() != null.
    //           If null → throw IllegalStateException("Proof image required").
    //
    //   Step 4: Set the new status and save.

    @Override
    public void transitionStatus(UUID reportId, ReportStatus newStatus) {
        // TODO 05c: Implement here
        Optional<WasteReport> byId = reportRepository.findById(reportId);
        WasteReport report = byId.orElseThrow(() -> new IllegalArgumentException("Report not found"));

        ReportStatus currentStatus = report.getStatus();
        ReportStatus allowedNextStatus = VALID_TRANSITIONS.get(currentStatus);


        //if current status is COLLECTED, then allowedNextStatus will be null, which means it's a terminal state and no transitions are allowed.
        if (allowedNextStatus == null) {
            throw new IllegalStateException("Current status is terminal, no transitions allowed");
        }
        if (!allowedNextStatus.equals(newStatus)) {
            throw new IllegalStateException("Invalid status transition from " + currentStatus + " to " + newStatus);

        }

        if (newStatus == ReportStatus.COLLECTED && report.getProofImagePath() == null) {
            throw new IllegalStateException("Proof image required for COLLECTED status");
        }
        report.setStatus(newStatus);
        reportRepository.save(report);
    }

    // TODO 10b: Implement claimReport(UUID reportId, UUID enterpriseUserId)
    //
    // **CURRENT** ← You are here
    //
    // Business Rule BR-02:
    //   Step 1: Find report by ID. Throw if not found.
    //   Step 2: Find user by enterpriseUserId. Throw if not found.
    //   Step 3: Check user.getRole() == UserRole.ENTERPRISE.
    //           If not → throw IllegalStateException("Only ENTERPRISE can claim reports")
    //   Step 4: Check report.getStatus() == ReportStatus.PENDING.
    //           If not → throw IllegalStateException("Can only claim PENDING reports")
    //   Step 5: Set report.setAcceptedBy(enterprise user)
    //   Step 6: Set report.setStatus(ReportStatus.ACCEPTED)
    //   Step 7: Save report.

    //Enterprise receive report from citizen
    @Override
    public void claimReport(UUID reportId, UUID enterpriseUserId) {
        // TODO 10b: Implement here
        WasteReport reportFoundByReportId = reportRepository.findById(reportId).orElseThrow(() -> new IllegalArgumentException("Report not found"));
        User userFoundByEnterpriseId = userRepository.findById(enterpriseUserId).orElseThrow(() -> new IllegalArgumentException("Enterprise user not found"));

        if (userFoundByEnterpriseId.getRole() != UserRole.ENTERPRISE) {
            throw new IllegalStateException("Only ENTERPRISE can claim reports");
        }
        if (reportFoundByReportId.getStatus() != ReportStatus.PENDING) {
            throw new IllegalStateException("Can only claim PENDING reports");
        }
        reportFoundByReportId.setAcceptedBy(userFoundByEnterpriseId);
        reportFoundByReportId.setStatus(ReportStatus.ACCEPTED);
        reportRepository.save(reportFoundByReportId);

    }


    // TODO 10c: Implement assignCollector(UUID reportId, UUID enterpriseUserId, UUID collectorUserId)
    //
    // Business Rule BR-03:
    //   Step 1: Find report by ID. Throw if not found.
    //   Step 2: Check report.getStatus() == ReportStatus.ACCEPTED.
    //           If not → throw IllegalStateException("Can only assign to ACCEPTED reports")
    //   Step 3: Find collector by collectorUserId. Throw if not found.
    //   Step 4: Check collector.getEmployer() != null
    //           AND collector.getEmployer().getId().equals(enterpriseUserId).
    //           If not → throw IllegalStateException("Collector does not belong to this enterprise")
    //   Step 5: Set report.setCollectedBy(collector)
    //   Step 6: Set report.setStatus(ReportStatus.ASSIGNED)
    //   Step 7: Save report.
    @Override
    public void assignCollector(UUID reportId, UUID enterpriseUserId, UUID collectorUserId) {
        // TODO 10c: Implement here
        WasteReport reportFoundById = reportRepository.findById(reportId).orElseThrow(() -> new IllegalArgumentException("Report not found"));
        if(ReportStatus.ACCEPTED != reportFoundById.getStatus()  ) {
            throw new IllegalStateException("Can only assign to ACCEPTED reports");
        }
        User foundCollector = userRepository.findById(collectorUserId).orElseThrow(() -> new IllegalArgumentException("Collector not found"));
        if(foundCollector.getEmployer() == null || !foundCollector.getEmployer().getId().equals(enterpriseUserId)) {
            throw new IllegalStateException("Collector does not belong to this enterprise");
        }

        reportFoundById.setCollectedBy(foundCollector);
        reportFoundById.setStatus(ReportStatus.ASSIGNED);
        reportRepository.save(reportFoundById);
    }
}



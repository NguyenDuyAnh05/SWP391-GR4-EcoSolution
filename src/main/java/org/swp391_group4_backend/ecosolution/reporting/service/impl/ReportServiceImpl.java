package org.swp391_group4_backend.ecosolution.reporting.service.impl;


import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;
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

    public ReportServiceImpl(WasteReportRepository reportRepository) {
        this.reportRepository = reportRepository;
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
}


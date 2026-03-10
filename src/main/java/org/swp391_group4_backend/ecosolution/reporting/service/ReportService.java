package org.swp391_group4_backend.ecosolution.reporting.service;

import java.util.UUID;
import org.swp391_group4_backend.ecosolution.reporting.domain.ReportStatus;

/**
 * TODO 04: Define the ReportService interface.
 *
 * - Contract for report lifecycle operations.
 * - The service layer is where business rules live.
 * - Implementations enforce the state machine (BR-01) and proof rule (BR-04).
 *
 * Method to add:
 *   void transitionStatus(UUID reportId, ReportStatus newStatus);
 */
public interface ReportService {
    // TODO 04: Add the transitionStatus method signature
    void transitionStatus(UUID reportId, ReportStatus newStatus);

    // TODO 08: Add claimReport method signature
    // Business Rule BR-02:
    //   - Only a user with role == ENTERPRISE can claim
    //   - Report must be in PENDING status
    //   - Sets report.acceptedBy = enterprise user
    //   - Transitions status from PENDING → ACCEPTED
    void claimReport(UUID reportId, UUID enterpriseUserId);

    // TODO 09: Add assignCollector method signature
    // Business Rule BR-03:
    //   - Report must be in ACCEPTED status
    //   - collector.employer.id must equal enterpriseUserId (own collector only)
    //   - Sets report.collectedBy = collector
    //   - Transitions status from ACCEPTED → ASSIGNED
    void assignCollector(UUID reportId, UUID enterpriseUserId, UUID collectorUserId);
}



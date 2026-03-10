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
}


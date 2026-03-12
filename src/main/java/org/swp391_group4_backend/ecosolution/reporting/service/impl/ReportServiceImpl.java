package org.swp391_group4_backend.ecosolution.reporting.service.impl;

import org.springframework.stereotype.Component;
import org.swp391_group4_backend.ecosolution.reporting.service.ReportService;
import org.swp391_group4_backend.ecosolution.reporting.domain.ReportStatus;

import java.util.UUID;

/**
 * Legacy stub implementation retained to satisfy old package references.
 * All actual feature logic has been moved to com.ecosolution.* packages.
 */
@Component
public class ReportServiceImpl implements ReportService {

    @Override
    public void transitionStatus(UUID reportId, ReportStatus newStatus) {
        throw new UnsupportedOperationException("Legacy stub - use com.ecosolution.reporting.service.ReportService instead");
    }

    @Override
    public void claimReport(UUID reportId, UUID enterpriseUserId) {
        throw new UnsupportedOperationException("Legacy stub - use com.ecosolution.reporting.service.ReportService instead");
    }

    @Override
    public void assignCollector(UUID reportId, UUID enterpriseUserId, UUID collectorUserId) {
        throw new UnsupportedOperationException("Legacy stub - use com.ecosolution.reporting.service.ReportService instead");
    }
}



package org.swp391_group4_backend.ecosolution.reporting.service;

import org.swp391_group4_backend.ecosolution.reporting.domain.entity.WasteReport;
import org.swp391_group4_backend.ecosolution.reporting.domain.request.ReportRequest;
import java.util.List;
import java.util.UUID;

public interface ReportService {
    WasteReport createReport(ReportRequest request);

    List<WasteReport> getReportsForCurrentUser();

    // Read methods to support controller views (keep repository access in services)
    java.util.List<WasteReport> findPendingReports();

    java.util.List<WasteReport> findAcceptedReports();

    java.util.List<WasteReport> findAssignedReports();

    java.util.List<WasteReport> findAssignedReportsForCurrentUser();

    void cancelReport(UUID id);

    // workflow operations
    void claimReport(UUID reportId);

    void assignCollector(UUID reportId, UUID collectorId);

    void completeCollection(org.swp391_group4_backend.ecosolution.reporting.domain.request.CollectionCompleteRequest req);
}


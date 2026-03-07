package org.swp391_group4_backend.ecosolution.reports.exception;

import java.util.UUID;

public class WasteReportNotFoundException extends RuntimeException {
  private final UUID reportId;

  public WasteReportNotFoundException(UUID reportId) {
    super("Waste report not found: " + reportId);
    this.reportId = reportId;
  }

  public UUID getReportId() {
    return reportId;
  }
}


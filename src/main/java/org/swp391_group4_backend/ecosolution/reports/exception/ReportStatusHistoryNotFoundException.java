package org.swp391_group4_backend.ecosolution.reports.exception;

import java.util.UUID;

public class ReportStatusHistoryNotFoundException extends RuntimeException {
  private final UUID historyId;

  public ReportStatusHistoryNotFoundException(UUID historyId) {
    super(\
Report
status
history
not
found:
\ + historyId);
    this.historyId = historyId;
  }

  public UUID getHistoryId() {
    return historyId;
  }
}

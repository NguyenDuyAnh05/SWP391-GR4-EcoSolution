package org.swp391_group4_backend.ecosolution.reports.exception;

import java.util.UUID;

public class WasteReportImageNotFoundException extends RuntimeException {
  private final UUID imageId;

  public WasteReportImageNotFoundException(UUID imageId) {
    super("Waste report image not found: " + imageId);
    this.imageId = imageId;
  }

  public UUID getImageId() {
    return imageId;
  }
}


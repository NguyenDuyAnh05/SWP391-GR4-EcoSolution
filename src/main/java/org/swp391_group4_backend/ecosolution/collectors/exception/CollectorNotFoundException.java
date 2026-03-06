package org.swp391_group4_backend.ecosolution.collectors.exception;

import java.util.UUID;

public class CollectorNotFoundException extends RuntimeException {
  private final UUID collectorId;

  public CollectorNotFoundException(UUID collectorId) {
    super("Collector not found: " + collectorId);
    this.collectorId = collectorId;
  }

  public UUID getCollectorId() {
    return collectorId;
  }
}

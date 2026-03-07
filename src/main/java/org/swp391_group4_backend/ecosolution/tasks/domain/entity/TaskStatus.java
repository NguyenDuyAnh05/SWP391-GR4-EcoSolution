package org.swp391_group4_backend.ecosolution.tasks.domain.entity;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Lifecycle states of a collecting task")
public enum TaskStatus {
  ASSIGNED,
  ACCEPTED,
  IN_PROGRESS,
  COMPLETED,
  CANCELLED
}

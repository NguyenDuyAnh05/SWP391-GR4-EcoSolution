package org.swp391_group4_backend.ecosolution.auth.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "collecting_task")
public class CollectingTask {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(nullable = false, updatable = false)
  private UUID id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "report_id", nullable = false)
  private WasteReport report;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "collector_id", nullable = false)
  private User collector;

  @Column(name = "current_status", nullable = false)
  @Enumerated(EnumType.STRING)
  private TaskStatus currentStatus;

  @Column(name = "assigned_at", nullable = false)
  private LocalDateTime assignedAt;

  @Column(name = "started_at")
  private LocalDateTime startedAt;

  @Column(name = "completed_at")
  private LocalDateTime completedAt;
}


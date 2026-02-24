package org.swp391_group4_backend.ecosolution.reports.domain.entity;
import org.swp391_group4_backend.ecosolution.auth.domain.entity.User;

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
@Table(name = "report_status_history")
public class ReportStatusHistory {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(nullable = false, updatable = false)
  private UUID id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "report_id", nullable = false)
  private WasteReport report;

  @Column(name = "status_from", nullable = false)
  @Enumerated(EnumType.STRING)
  private ReportStatus statusFrom;

  @Column(name = "status_to", nullable = false)
  @Enumerated(EnumType.STRING)
  private ReportStatus statusTo;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "changed_by", nullable = false)
  private User changedBy;

  @Column(columnDefinition = "TEXT")
  private String reason;

  @Column(name = "changed_at", nullable = false)
  private LocalDateTime changedAt;
}




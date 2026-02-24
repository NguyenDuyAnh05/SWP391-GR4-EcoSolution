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
@Table(name = "complaint")
public class Complaint {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(nullable = false, updatable = false)
  private UUID id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "report_id", nullable = false)
  private WasteReport report;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "citizen_id", nullable = false)
  private User citizen;

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private ComplaintType type;

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private ComplaintStatus status;

  @Column(nullable = false, columnDefinition = "TEXT")
  private String description;

  @Column(name = "created_at", nullable = false)
  private LocalDateTime createdAt;

  @Column(name = "resolved_at")
  private LocalDateTime resolvedAt;
}


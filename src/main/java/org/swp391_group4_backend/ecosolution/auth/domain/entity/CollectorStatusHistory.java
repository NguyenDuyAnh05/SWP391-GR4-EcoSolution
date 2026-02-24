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
@Table(name = "collector_status_history")
public class CollectorStatusHistory {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(nullable = false, updatable = false)
  private UUID id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "collector_id", nullable = false)
  private User collector;

  @Column(name = "status_from", nullable = false)
  @Enumerated(EnumType.STRING)
  private TaskStatus statusFrom;

  @Column(name = "status_to", nullable = false)
  @Enumerated(EnumType.STRING)
  private TaskStatus statusTo;

  @Column(columnDefinition = "TEXT")
  private String reason;

  @Column(name = "changed_at", nullable = false)
  private LocalDateTime changedAt;
}


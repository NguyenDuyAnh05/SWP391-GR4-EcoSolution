package org.swp391_group4_backend.ecosolution.collectors.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.swp391_group4_backend.ecosolution.collectors.domain.entity.CollectorStatusHistory;
import org.swp391_group4_backend.ecosolution.tasks.domain.entity.TaskStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface CollectorStatusHistoryRepository extends JpaRepository<CollectorStatusHistory, UUID> {

  // Find history for a specific collector, ordered by change date
  List<CollectorStatusHistory> findByCollectorIdOrderByChangedAtDesc(UUID collectorId);

  // Find history by status transition
  List<CollectorStatusHistory> findByStatusFromAndStatusTo(TaskStatus from, TaskStatus to);

  // Find recent status changes
  List<CollectorStatusHistory> findByChangedAtAfterOrderByChangedAtDesc(LocalDateTime date);

  // Get latest status change for a collector
  @Query("SELECT h FROM CollectorStatusHistory h WHERE h.collector.id = :collectorId " +
         "ORDER BY h.changedAt DESC LIMIT 1")
  CollectorStatusHistory findLatestByCollectorId(UUID collectorId);

  // Find status changes within date range
  List<CollectorStatusHistory> findByChangedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
}




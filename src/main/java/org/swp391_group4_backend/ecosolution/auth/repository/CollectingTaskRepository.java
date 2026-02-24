package org.swp391_group4_backend.ecosolution.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.swp391_group4_backend.ecosolution.auth.domain.entity.CollectingTask;
import org.swp391_group4_backend.ecosolution.auth.domain.entity.TaskStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CollectingTaskRepository extends JpaRepository<CollectingTask, UUID> {

  // Find task by report ID
  Optional<CollectingTask> findByReportId(UUID reportId);

  // Find tasks by collector
  List<CollectingTask> findByCollectorId(UUID collectorId);

  // Find tasks by status
  List<CollectingTask> findByCurrentStatus(TaskStatus status);

  // Find tasks by collector and status
  List<CollectingTask> findByCollectorIdAndCurrentStatus(UUID collectorId, TaskStatus status);

  // Find active tasks for a collector
  @Query("SELECT t FROM CollectingTask t WHERE t.collector.id = :collectorId " +
         "AND t.currentStatus IN ('ASSIGNED', 'ACCEPTED', 'IN_PROGRESS')")
  List<CollectingTask> findActiveTasksByCollector(UUID collectorId);

  // Find tasks assigned within date range
  List<CollectingTask> findByAssignedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

  // Find completed tasks within date range
  List<CollectingTask> findByCompletedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

  // Count tasks by collector
  long countByCollectorId(UUID collectorId);

  // Count tasks by collector and status
  long countByCollectorIdAndCurrentStatus(UUID collectorId, TaskStatus status);

  // Find overdue tasks
  @Query("SELECT t FROM CollectingTask t WHERE t.currentStatus = 'ASSIGNED' " +
         "AND t.assignedAt < :deadline AND t.startedAt IS NULL")
  List<CollectingTask> findOverdueTasks(LocalDateTime deadline);

  // Check if report already has a task
  boolean existsByReportId(UUID reportId);
}


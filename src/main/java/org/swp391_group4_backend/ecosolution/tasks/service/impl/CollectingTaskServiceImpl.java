package org.swp391_group4_backend.ecosolution.tasks.service.impl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.swp391_group4_backend.ecosolution.auth.domain.entity.User;
import org.swp391_group4_backend.ecosolution.auth.domain.entity.UserRole;
import org.swp391_group4_backend.ecosolution.auth.repository.UserRepository;
import org.swp391_group4_backend.ecosolution.reports.domain.entity.ReportStatus;
import org.swp391_group4_backend.ecosolution.reports.domain.entity.WasteReport;
import org.swp391_group4_backend.ecosolution.reports.repository.WasteReportRepository;
import org.swp391_group4_backend.ecosolution.tasks.domain.dto.request.CollectingTaskAssignRequestDto;
import org.swp391_group4_backend.ecosolution.tasks.domain.entity.CollectingTask;
import org.swp391_group4_backend.ecosolution.tasks.domain.entity.TaskStatus;
import org.swp391_group4_backend.ecosolution.tasks.exception.InvalidTaskTransitionException;
import org.swp391_group4_backend.ecosolution.tasks.exception.TaskAssignmentException;
import org.swp391_group4_backend.ecosolution.tasks.exception.TaskNotFoundException;
import org.swp391_group4_backend.ecosolution.tasks.repository.CollectingTaskRepository;
import org.swp391_group4_backend.ecosolution.tasks.service.CollectingTaskService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
@Service
public class CollectingTaskServiceImpl implements CollectingTaskService {
  private final CollectingTaskRepository collectingTaskRepository;
  private final WasteReportRepository wasteReportRepository;
  private final UserRepository userRepository;
  public CollectingTaskServiceImpl(
      CollectingTaskRepository collectingTaskRepository,
      WasteReportRepository wasteReportRepository,
      UserRepository userRepository
  ) {
    this.collectingTaskRepository = collectingTaskRepository;
    this.wasteReportRepository = wasteReportRepository;
    this.userRepository = userRepository;
  }
  @Override
  @Transactional
  public CollectingTask create(CollectingTask collectingTask) {
    return collectingTaskRepository.save(collectingTask);
  }
  @Override
  @Transactional(readOnly = true)
  public Optional<CollectingTask> getById(UUID id) {
    return collectingTaskRepository.findById(id);
  }
  @Override
  @Transactional(readOnly = true)
  public List<CollectingTask> getAll() {
    return collectingTaskRepository.findAll();
  }
  @Override
  @Transactional(readOnly = true)
  public List<CollectingTask> getAll(UUID collectorId, TaskStatus status) {
    if (collectorId != null && status != null) {
      return collectingTaskRepository.findByCollectorIdAndCurrentStatus(collectorId, status);
    }
    if (collectorId != null) {
      return collectingTaskRepository.findByCollectorId(collectorId);
    }
    if (status != null) {
      return collectingTaskRepository.findByCurrentStatus(status);
    }
    return collectingTaskRepository.findAll();
  }
  @Override
  @Transactional
  public CollectingTask assignTask(CollectingTaskAssignRequestDto requestDto) {
    if (collectingTaskRepository.existsByReportId(requestDto.reportId())) {
      throw new TaskAssignmentException("Report " + requestDto.reportId() + " is already assigned to a task");
    }
    WasteReport report = wasteReportRepository.findById(requestDto.reportId())
        .orElseThrow(() -> new TaskAssignmentException("Report " + requestDto.reportId() + " not found"));
    User collector = userRepository.findById(requestDto.collectorId())
        .orElseThrow(() -> new TaskAssignmentException("Collector " + requestDto.collectorId() + " not found"));
    if (collector.getRole() != UserRole.COLLECTOR) {
      throw new TaskAssignmentException("User " + requestDto.collectorId() + " is not a collector");
    }
    CollectingTask collectingTask = CollectingTask.builder()
        .report(report)
        .collector(collector)
        .currentStatus(TaskStatus.ASSIGNED)
        .assignedAt(LocalDateTime.now())
        .build();
    report.setCurrentStatus(ReportStatus.ASSIGNED);
    wasteReportRepository.save(report);
    return collectingTaskRepository.save(collectingTask);
  }
  @Override
  @Transactional
  public CollectingTask updateStatus(UUID id, TaskStatus status) {
    CollectingTask collectingTask = collectingTaskRepository.findById(id)
        .orElseThrow(() -> new TaskNotFoundException(id));
    validateTransition(collectingTask.getCurrentStatus(), status);
    collectingTask.setCurrentStatus(status);
    if (status == TaskStatus.IN_PROGRESS && collectingTask.getStartedAt() == null) {
      collectingTask.setStartedAt(LocalDateTime.now());
    }
    if (status == TaskStatus.COMPLETED) {
      if (collectingTask.getStartedAt() == null) {
        collectingTask.setStartedAt(LocalDateTime.now());
      }
      collectingTask.setCompletedAt(LocalDateTime.now());
    }
    collectingTask.getReport().setCurrentStatus(mapTaskStatusToReportStatus(status));
    return collectingTaskRepository.save(collectingTask);
  }
  @Override
  @Transactional
  public CollectingTask update(UUID id, CollectingTask collectingTask) {
    if (!collectingTaskRepository.existsById(id)) {
      throw new TaskNotFoundException(id);
    }
    collectingTask.setId(id);
    return collectingTaskRepository.save(collectingTask);
  }
  @Override
  @Transactional
  public void delete(UUID id) {
    if (!collectingTaskRepository.existsById(id)) {
      throw new TaskNotFoundException(id);
    }
    collectingTaskRepository.deleteById(id);
  }
  private void validateTransition(TaskStatus currentStatus, TaskStatus targetStatus) {
    if (currentStatus == targetStatus) {
      return;
    }
    boolean isValid = switch (currentStatus) {
      case ASSIGNED -> targetStatus == TaskStatus.ACCEPTED || targetStatus == TaskStatus.CANCELLED;
      case ACCEPTED -> targetStatus == TaskStatus.IN_PROGRESS || targetStatus == TaskStatus.CANCELLED;
      case IN_PROGRESS -> targetStatus == TaskStatus.COMPLETED || targetStatus == TaskStatus.CANCELLED;
      case COMPLETED, CANCELLED -> false;
    };
    if (!isValid) {
      throw new InvalidTaskTransitionException(currentStatus, targetStatus);
    }
  }
  private ReportStatus mapTaskStatusToReportStatus(TaskStatus taskStatus) {
    return switch (taskStatus) {
      case ASSIGNED, ACCEPTED -> ReportStatus.ASSIGNED;
      case IN_PROGRESS -> ReportStatus.IN_PROGRESS;
      case COMPLETED -> ReportStatus.COMPLETED;
      case CANCELLED -> ReportStatus.CANCELLED;
    };
  }
}

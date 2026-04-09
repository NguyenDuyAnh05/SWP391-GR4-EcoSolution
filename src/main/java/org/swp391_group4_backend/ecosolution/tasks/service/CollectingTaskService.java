package org.swp391_group4_backend.ecosolution.tasks.service;
import org.swp391_group4_backend.ecosolution.tasks.domain.dto.request.CollectingTaskAssignRequestDto;
import org.swp391_group4_backend.ecosolution.tasks.domain.entity.CollectingTask;
import org.swp391_group4_backend.ecosolution.tasks.domain.entity.TaskStatus;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
public interface CollectingTaskService {
  CollectingTask create(CollectingTask collectingTask);
  Optional<CollectingTask> getById(UUID id);
  List<CollectingTask> getAll();
  List<CollectingTask> getAll(UUID collectorId, TaskStatus status);
  CollectingTask assignTask(CollectingTaskAssignRequestDto requestDto);
  CollectingTask updateStatus(UUID id, TaskStatus status);
  CollectingTask update(UUID id, CollectingTask collectingTask);
  void delete(UUID id);
}

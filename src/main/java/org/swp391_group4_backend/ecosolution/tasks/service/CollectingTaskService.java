package org.swp391_group4_backend.ecosolution.tasks.service;

import org.swp391_group4_backend.ecosolution.tasks.domain.entity.CollectingTask;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CollectingTaskService {
  CollectingTask create(CollectingTask collectingTask);

  Optional<CollectingTask> getById(UUID id);

  List<CollectingTask> getAll();

  CollectingTask update(UUID id, CollectingTask collectingTask);

  void delete(UUID id);
}




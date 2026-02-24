package org.swp391_group4_backend.ecosolution.tasks.service.impl;

import org.springframework.stereotype.Service;
import org.swp391_group4_backend.ecosolution.tasks.domain.entity.CollectingTask;
import org.swp391_group4_backend.ecosolution.tasks.repository.CollectingTaskRepository;
import org.swp391_group4_backend.ecosolution.tasks.service.CollectingTaskService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CollectingTaskServiceImpl implements CollectingTaskService {
  private final CollectingTaskRepository collectingTaskRepository;

  public CollectingTaskServiceImpl(CollectingTaskRepository collectingTaskRepository) {
    this.collectingTaskRepository = collectingTaskRepository;
  }

  @Override
  public CollectingTask create(CollectingTask collectingTask) {
    return collectingTaskRepository.save(collectingTask);
  }

  @Override
  public Optional<CollectingTask> getById(UUID id) {
    return collectingTaskRepository.findById(id);
  }

  @Override
  public List<CollectingTask> getAll() {
    return collectingTaskRepository.findAll();
  }

  @Override
  public CollectingTask update(UUID id, CollectingTask collectingTask) {
    collectingTask.setId(id);
    return collectingTaskRepository.save(collectingTask);
  }

  @Override
  public void delete(UUID id) {
    collectingTaskRepository.deleteById(id);
  }
}




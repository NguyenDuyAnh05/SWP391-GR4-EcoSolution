package org.swp391_group4_backend.ecosolution.collectors.service.impl;

import org.springframework.stereotype.Service;
import org.swp391_group4_backend.ecosolution.collectors.domain.entity.CollectorStatusHistory;
import org.swp391_group4_backend.ecosolution.collectors.repository.CollectorStatusHistoryRepository;
import org.swp391_group4_backend.ecosolution.collectors.service.CollectorStatusHistoryService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CollectorStatusHistoryServiceImpl implements CollectorStatusHistoryService {
  private final CollectorStatusHistoryRepository collectorStatusHistoryRepository;

  public CollectorStatusHistoryServiceImpl(CollectorStatusHistoryRepository collectorStatusHistoryRepository) {
    this.collectorStatusHistoryRepository = collectorStatusHistoryRepository;
  }

  @Override
  public CollectorStatusHistory create(CollectorStatusHistory collectorStatusHistory) {
    return collectorStatusHistoryRepository.save(collectorStatusHistory);
  }

  @Override
  public Optional<CollectorStatusHistory> getById(UUID id) {
    return collectorStatusHistoryRepository.findById(id);
  }

  @Override
  public List<CollectorStatusHistory> getAll() {
    return collectorStatusHistoryRepository.findAll();
  }

  @Override
  public CollectorStatusHistory update(UUID id, CollectorStatusHistory collectorStatusHistory) {
    collectorStatusHistory.setId(id);
    return collectorStatusHistoryRepository.save(collectorStatusHistory);
  }

  @Override
  public void delete(UUID id) {
    collectorStatusHistoryRepository.deleteById(id);
  }
}




package org.swp391_group4_backend.ecosolution.collectors.service;

import org.swp391_group4_backend.ecosolution.collectors.domain.entity.CollectorStatusHistory;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CollectorStatusHistoryService {
  CollectorStatusHistory create(CollectorStatusHistory collectorStatusHistory);

  Optional<CollectorStatusHistory> getById(UUID id);

  List<CollectorStatusHistory> getAll();

  CollectorStatusHistory update(UUID id, CollectorStatusHistory collectorStatusHistory);

  void delete(UUID id);
}



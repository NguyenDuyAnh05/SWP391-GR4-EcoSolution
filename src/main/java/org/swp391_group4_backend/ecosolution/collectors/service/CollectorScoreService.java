package org.swp391_group4_backend.ecosolution.collectors.service;

import org.swp391_group4_backend.ecosolution.collectors.domain.entity.CollectorScore;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CollectorScoreService {
  CollectorScore create(CollectorScore collectorScore);

  Optional<CollectorScore> getById(UUID id);

  List<CollectorScore> getAll();

  CollectorScore update(UUID id, CollectorScore collectorScore);

  void delete(UUID id);
}




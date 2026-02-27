package org.swp391_group4_backend.ecosolution.collectors.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.data.domain.PageRequest;
import org.swp391_group4_backend.ecosolution.collectors.domain.entity.CollectorScore;
import org.swp391_group4_backend.ecosolution.collectors.repository.CollectorScoreRepository;
import org.swp391_group4_backend.ecosolution.collectors.service.CollectorScoreService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CollectorScoreServiceImpl implements CollectorScoreService {
  private final CollectorScoreRepository collectorScoreRepository;

  public CollectorScoreServiceImpl(CollectorScoreRepository collectorScoreRepository) {
    this.collectorScoreRepository = collectorScoreRepository;
  }

  @Override
  public CollectorScore create(CollectorScore collectorScore) {
    return collectorScoreRepository.save(collectorScore);
  }

  @Override
  public Optional<CollectorScore> getById(UUID id) {
    return collectorScoreRepository.findById(id);
  }

  @Override
  public List<CollectorScore> getAll() {
    return collectorScoreRepository.findAll();
  }

  @Override
  public CollectorScore update(UUID id, CollectorScore collectorScore) {
    collectorScore.setCollectorId(id);
    return collectorScoreRepository.save(collectorScore);
  }

  @Override
  public void delete(UUID id) {
    collectorScoreRepository.deleteById(id);
  }

  @Override
  public List<CollectorScore> findTopCollectorsByReliability(int limit) {
    return collectorScoreRepository.findTopCollectorsByReliability(PageRequest.of(0, limit));
  }
}

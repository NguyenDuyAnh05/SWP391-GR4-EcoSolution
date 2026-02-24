package org.swp391_group4_backend.ecosolution.fraud.service.impl;

import org.springframework.stereotype.Service;
import org.swp391_group4_backend.ecosolution.fraud.domain.entity.FraudSignal;
import org.swp391_group4_backend.ecosolution.fraud.repository.FraudSignalRepository;
import org.swp391_group4_backend.ecosolution.fraud.service.FraudSignalService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class FraudSignalServiceImpl implements FraudSignalService {
  private final FraudSignalRepository fraudSignalRepository;

  public FraudSignalServiceImpl(FraudSignalRepository fraudSignalRepository) {
    this.fraudSignalRepository = fraudSignalRepository;
  }

  @Override
  public FraudSignal create(FraudSignal fraudSignal) {
    return fraudSignalRepository.save(fraudSignal);
  }

  @Override
  public Optional<FraudSignal> getById(UUID id) {
    return fraudSignalRepository.findById(id);
  }

  @Override
  public List<FraudSignal> getAll() {
    return fraudSignalRepository.findAll();
  }

  @Override
  public FraudSignal update(UUID id, FraudSignal fraudSignal) {
    fraudSignal.setId(id);
    return fraudSignalRepository.save(fraudSignal);
  }

  @Override
  public void delete(UUID id) {
    fraudSignalRepository.deleteById(id);
  }
}




package org.swp391_group4_backend.ecosolution.fraud.service;

import org.swp391_group4_backend.ecosolution.fraud.domain.entity.FraudSignal;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FraudSignalService {
  FraudSignal create(FraudSignal fraudSignal);

  Optional<FraudSignal> getById(UUID id);

  List<FraudSignal> getAll();

  FraudSignal update(UUID id, FraudSignal fraudSignal);

  void delete(UUID id);
}




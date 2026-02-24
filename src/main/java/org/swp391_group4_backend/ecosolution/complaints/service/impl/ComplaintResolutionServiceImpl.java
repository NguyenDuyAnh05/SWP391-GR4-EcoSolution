package org.swp391_group4_backend.ecosolution.complaints.service.impl;

import org.springframework.stereotype.Service;
import org.swp391_group4_backend.ecosolution.complaints.domain.entity.ComplaintResolution;
import org.swp391_group4_backend.ecosolution.complaints.repository.ComplaintResolutionRepository;
import org.swp391_group4_backend.ecosolution.complaints.service.ComplaintResolutionService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ComplaintResolutionServiceImpl implements ComplaintResolutionService {
  private final ComplaintResolutionRepository complaintResolutionRepository;

  public ComplaintResolutionServiceImpl(ComplaintResolutionRepository complaintResolutionRepository) {
    this.complaintResolutionRepository = complaintResolutionRepository;
  }

  @Override
  public ComplaintResolution create(ComplaintResolution complaintResolution) {
    return complaintResolutionRepository.save(complaintResolution);
  }

  @Override
  public Optional<ComplaintResolution> getById(UUID id) {
    return complaintResolutionRepository.findById(id);
  }

  @Override
  public List<ComplaintResolution> getAll() {
    return complaintResolutionRepository.findAll();
  }

  @Override
  public ComplaintResolution update(UUID id, ComplaintResolution complaintResolution) {
    complaintResolution.setId(id);
    return complaintResolutionRepository.save(complaintResolution);
  }

  @Override
  public void delete(UUID id) {
    complaintResolutionRepository.deleteById(id);
  }
}




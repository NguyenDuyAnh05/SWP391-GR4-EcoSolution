package org.swp391_group4_backend.ecosolution.complaints.service;

import org.swp391_group4_backend.ecosolution.complaints.domain.entity.ComplaintResolution;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ComplaintResolutionService {
  ComplaintResolution create(ComplaintResolution complaintResolution);

  Optional<ComplaintResolution> getById(UUID id);

  List<ComplaintResolution> getAll();

  ComplaintResolution update(UUID id, ComplaintResolution complaintResolution);

  void delete(UUID id);
}




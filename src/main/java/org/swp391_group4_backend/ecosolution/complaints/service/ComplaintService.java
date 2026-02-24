package org.swp391_group4_backend.ecosolution.complaints.service;

import org.swp391_group4_backend.ecosolution.complaints.domain.entity.Complaint;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ComplaintService {
  Complaint create(Complaint complaint);

  Optional<Complaint> getById(UUID id);

  List<Complaint> getAll();

  Complaint update(UUID id, Complaint complaint);

  void delete(UUID id);
}




package org.swp391_group4_backend.ecosolution.complaints.service.impl;

import org.springframework.stereotype.Service;
import org.swp391_group4_backend.ecosolution.complaints.domain.entity.Complaint;
import org.swp391_group4_backend.ecosolution.complaints.repository.ComplaintRepository;
import org.swp391_group4_backend.ecosolution.complaints.service.ComplaintService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ComplaintServiceImpl implements ComplaintService {
  private final ComplaintRepository complaintRepository;

  public ComplaintServiceImpl(ComplaintRepository complaintRepository) {
    this.complaintRepository = complaintRepository;
  }

  @Override
  public Complaint create(Complaint complaint) {
    return complaintRepository.save(complaint);
  }

  @Override
  public Optional<Complaint> getById(UUID id) {
    return complaintRepository.findById(id);
  }

  @Override
  public List<Complaint> getAll() {
    return complaintRepository.findAll();
  }

  @Override
  public Complaint update(UUID id, Complaint complaint) {
    complaint.setId(id);
    return complaintRepository.save(complaint);
  }

  @Override
  public void delete(UUID id) {
    complaintRepository.deleteById(id);
  }
}




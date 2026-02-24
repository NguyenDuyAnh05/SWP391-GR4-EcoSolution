package org.swp391_group4_backend.ecosolution.reports.service.impl;

import org.springframework.stereotype.Service;
import org.swp391_group4_backend.ecosolution.reports.domain.entity.ReportStatusHistory;
import org.swp391_group4_backend.ecosolution.reports.repository.ReportStatusHistoryRepository;
import org.swp391_group4_backend.ecosolution.reports.service.ReportStatusHistoryService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ReportStatusHistoryServiceImpl implements ReportStatusHistoryService {
  private final ReportStatusHistoryRepository reportStatusHistoryRepository;

  public ReportStatusHistoryServiceImpl(ReportStatusHistoryRepository reportStatusHistoryRepository) {
    this.reportStatusHistoryRepository = reportStatusHistoryRepository;
  }

  @Override
  public ReportStatusHistory create(ReportStatusHistory reportStatusHistory) {
    return reportStatusHistoryRepository.save(reportStatusHistory);
  }

  @Override
  public Optional<ReportStatusHistory> getById(UUID id) {
    return reportStatusHistoryRepository.findById(id);
  }

  @Override
  public List<ReportStatusHistory> getAll() {
    return reportStatusHistoryRepository.findAll();
  }

  @Override
  public ReportStatusHistory update(UUID id, ReportStatusHistory reportStatusHistory) {
    reportStatusHistory.setId(id);
    return reportStatusHistoryRepository.save(reportStatusHistory);
  }

  @Override
  public void delete(UUID id) {
    reportStatusHistoryRepository.deleteById(id);
  }
}




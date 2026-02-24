package org.swp391_group4_backend.ecosolution.reports.service;

import org.swp391_group4_backend.ecosolution.reports.domain.entity.ReportStatusHistory;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReportStatusHistoryService {
  ReportStatusHistory create(ReportStatusHistory reportStatusHistory);

  Optional<ReportStatusHistory> getById(UUID id);

  List<ReportStatusHistory> getAll();

  ReportStatusHistory update(UUID id, ReportStatusHistory reportStatusHistory);

  void delete(UUID id);
}




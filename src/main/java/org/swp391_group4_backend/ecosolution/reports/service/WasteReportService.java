package org.swp391_group4_backend.ecosolution.reports.service;

import org.swp391_group4_backend.ecosolution.reports.domain.entity.WasteReport;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WasteReportService {
  WasteReport create(WasteReport wasteReport);

  Optional<WasteReport> getById(UUID id);

  List<WasteReport> getAll();

  WasteReport update(UUID id, WasteReport wasteReport);

  void delete(UUID id);
}




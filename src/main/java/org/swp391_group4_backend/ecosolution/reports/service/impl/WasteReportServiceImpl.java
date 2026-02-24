package org.swp391_group4_backend.ecosolution.reports.service.impl;

import org.springframework.stereotype.Service;
import org.swp391_group4_backend.ecosolution.reports.domain.entity.WasteReport;
import org.swp391_group4_backend.ecosolution.reports.repository.WasteReportRepository;
import org.swp391_group4_backend.ecosolution.reports.service.WasteReportService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class WasteReportServiceImpl implements WasteReportService {
  private final WasteReportRepository wasteReportRepository;

  public WasteReportServiceImpl(WasteReportRepository wasteReportRepository) {
    this.wasteReportRepository = wasteReportRepository;
  }

  @Override
  public WasteReport create(WasteReport wasteReport) {
    return wasteReportRepository.save(wasteReport);
  }

  @Override
  public Optional<WasteReport> getById(UUID id) {
    return wasteReportRepository.findById(id);
  }

  @Override
  public List<WasteReport> getAll() {
    return wasteReportRepository.findAll();
  }

  @Override
  public WasteReport update(UUID id, WasteReport wasteReport) {
    wasteReport.setId(id);
    return wasteReportRepository.save(wasteReport);
  }

  @Override
  public void delete(UUID id) {
    wasteReportRepository.deleteById(id);
  }
}




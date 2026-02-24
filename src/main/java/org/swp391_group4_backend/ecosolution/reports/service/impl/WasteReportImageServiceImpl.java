package org.swp391_group4_backend.ecosolution.reports.service.impl;

import org.springframework.stereotype.Service;
import org.swp391_group4_backend.ecosolution.reports.domain.entity.WasteReportImage;
import org.swp391_group4_backend.ecosolution.reports.repository.WasteReportImageRepository;
import org.swp391_group4_backend.ecosolution.reports.service.WasteReportImageService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class WasteReportImageServiceImpl implements WasteReportImageService {
  private final WasteReportImageRepository wasteReportImageRepository;

  public WasteReportImageServiceImpl(WasteReportImageRepository wasteReportImageRepository) {
    this.wasteReportImageRepository = wasteReportImageRepository;
  }

  @Override
  public WasteReportImage create(WasteReportImage wasteReportImage) {
    return wasteReportImageRepository.save(wasteReportImage);
  }

  @Override
  public Optional<WasteReportImage> getById(UUID id) {
    return wasteReportImageRepository.findById(id);
  }

  @Override
  public List<WasteReportImage> getAll() {
    return wasteReportImageRepository.findAll();
  }

  @Override
  public WasteReportImage update(UUID id, WasteReportImage wasteReportImage) {
    wasteReportImage.setId(id);
    return wasteReportImageRepository.save(wasteReportImage);
  }

  @Override
  public void delete(UUID id) {
    wasteReportImageRepository.deleteById(id);
  }
}




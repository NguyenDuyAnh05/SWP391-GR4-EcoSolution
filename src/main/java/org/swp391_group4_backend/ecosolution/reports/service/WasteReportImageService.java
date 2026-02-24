package org.swp391_group4_backend.ecosolution.reports.service;

import org.swp391_group4_backend.ecosolution.reports.domain.entity.WasteReportImage;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WasteReportImageService {
  WasteReportImage create(WasteReportImage wasteReportImage);

  Optional<WasteReportImage> getById(UUID id);

  List<WasteReportImage> getAll();

  WasteReportImage update(UUID id, WasteReportImage wasteReportImage);

  void delete(UUID id);
}




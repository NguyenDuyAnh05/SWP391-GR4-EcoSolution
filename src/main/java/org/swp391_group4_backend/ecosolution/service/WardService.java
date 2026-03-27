package org.swp391_group4_backend.ecosolution.service;

import org.swp391_group4_backend.ecosolution.entity.Ward;

import java.util.List;

public interface WardService {
    List<Ward> getAllWards();
    Ward getWardById(Long id);
    Ward assignCollectorToWard(Long wardId, Long collectorId);
    Ward createWard(Ward ward);
    Ward updateWard(Long wardId, Ward wardDetails);
}

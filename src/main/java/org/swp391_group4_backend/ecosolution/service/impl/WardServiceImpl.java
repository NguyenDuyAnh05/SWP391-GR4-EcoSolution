package org.swp391_group4_backend.ecosolution.service.impl;

import org.springframework.stereotype.Service;
import org.swp391_group4_backend.ecosolution.entity.Ward;
import org.swp391_group4_backend.ecosolution.repository.WardRepository;
import org.swp391_group4_backend.ecosolution.service.WardService;

import java.util.List;

@Service
public class WardServiceImpl implements WardService {
    public WardServiceImpl(WardRepository wardRepository) {
        this.wardRepository = wardRepository;
    }

    private final WardRepository wardRepository;
    @Override
    public List<Ward> getAllWards() {
        return wardRepository.findAll();
    }

    @Override
    public Ward getWardById(Long id) {
        return wardRepository.findById(id).orElseThrow(() -> new RuntimeException("Ward not found"));
    }
}

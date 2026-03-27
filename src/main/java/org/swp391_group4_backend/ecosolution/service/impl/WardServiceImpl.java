package org.swp391_group4_backend.ecosolution.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.swp391_group4_backend.ecosolution.entity.User;
import org.swp391_group4_backend.ecosolution.entity.Ward;
import org.swp391_group4_backend.ecosolution.repository.UserRepository;
import org.swp391_group4_backend.ecosolution.repository.WardRepository;
import org.swp391_group4_backend.ecosolution.service.WardService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WardServiceImpl implements WardService {
    private final WardRepository wardRepository;
    private final UserRepository userRepository;



    @Override
    public List<Ward> getAllWards() {
        return wardRepository.findAll();
    }

    @Override
    public Ward getWardById(Long id) {
        return wardRepository.findById(id).orElseThrow(() -> new RuntimeException("Ward not found"));
    }

    // Assign Colletor to ward
    @Override
    public Ward assignCollectorToWard(Long wardId, Long collectorId) {
// 1. Tìm Phường
        Ward ward = wardRepository.findById(wardId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Phường này!"));

        // 2. Tìm Nhân viên (kiểm tra xem có đúng là Collector không)
        User collector = userRepository.findById(collectorId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Nhân viên này!"));

        // Bạn có thể thêm logic kiểm tra Role ở đây: if (!collector.getRole().equals("COLLECTOR")) throw ...

        // 3. Gán và Lưu vào DB
        ward.setCollector(collector);
        return wardRepository.save(ward);    
    }

    @Override
    public Ward createWard(Ward ward) {
        return wardRepository.save(ward);
    }

    @Override
    public Ward updateWard(Long wardId, Ward wardDetails) {
        Ward existingWard = getWardById(wardId);
        existingWard.setWardName(wardDetails.getWardName());
        return wardRepository.save(existingWard);
    }
}

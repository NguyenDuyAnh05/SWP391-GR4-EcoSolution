package org.swp391_group4_backend.ecosolution.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.swp391_group4_backend.ecosolution.constant.PointTransactionType;
import org.swp391_group4_backend.ecosolution.constant.TrashReportStatus;
import org.swp391_group4_backend.ecosolution.constant.WasteType;
import org.swp391_group4_backend.ecosolution.dto.request.TrashWeightInput;
import org.swp391_group4_backend.ecosolution.dto.response.TrashReportResponse;
import org.swp391_group4_backend.ecosolution.entity.*;
import org.swp391_group4_backend.ecosolution.repository.*;
import org.swp391_group4_backend.ecosolution.service.TrashReportService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TrashReportServiceImpl implements TrashReportService {

    private final TrashReportRepository trashReportRepository;
    private final TrashReportDetailRepository trashReportDetailRepository;
    private final UserRepository userRepository;
    private final PointHistoryRepository pointHistoryRepository;
    
    // Inject Repository cấu hình tỷ lệ điểm chuẩn hóa
    private final WasteTypeRateRepository wasteTypeRateRepository;

    @Override
    @Transactional
    public void confirmAndCalculatePoints(Long reportId, List<TrashWeightInput> weightsInput) {
        TrashReport report = trashReportRepository.findById(reportId)
                .orElseThrow(() -> new RuntimeException("Trash Report not found"));

        if (report.getStatus() == TrashReportStatus.CONFIRMED) {
            throw new RuntimeException("Trash Report has already been confirmed");
        }

        // Lấy tất cả tỷ lệ điểm hiện hành từ Database lên Cache Map O(1) bằng WasteType
        Map<WasteType, Integer> categoryRates = wasteTypeRateRepository.findAll().stream()
                .collect(Collectors.toMap(WasteTypeRate::getWasteType, WasteTypeRate::getPointsPerKg));

        int totalPoints = 0;
        List<TrashReportDetail> detailsToSave = new ArrayList<>();

        for (TrashWeightInput input : weightsInput) {
            WasteType category = input.getCategory();
            
            // GET rate tự động cho WasteType tương ứng
            int pointsPerKg = categoryRates.getOrDefault(category, 0);
            
            // Tính điểm = Số Kg * Rate
            int pointsForCategory = (int) (input.getWeightInKg() * pointsPerKg);
            totalPoints += pointsForCategory;

            TrashReportDetail detail = TrashReportDetail.builder()
                    .report(report)
                    .trashCategory(category)
                    .weightInKg(input.getWeightInKg())
                    .pointsCalculated(pointsForCategory)
                    .build();
            
            detailsToSave.add(detail);
        }

        trashReportDetailRepository.saveAll(detailsToSave);

        report.setStatus(TrashReportStatus.CONFIRMED);
        report.setTotalPointsEarned(totalPoints);
        trashReportRepository.save(report);

        User citizen = report.getCitizen();
        int currentPoints = citizen.getRewardPoints() != null ? citizen.getRewardPoints() : 0;
        citizen.setRewardPoints(currentPoints + totalPoints);
        userRepository.save(citizen);

        PointHistory history = PointHistory.builder()
                .user(citizen)
                .amount(totalPoints)
                .transactionType(PointTransactionType.EARN_FROM_TRASH)
                .referenceId(report.getId())
                .build();
                
        pointHistoryRepository.save(history);
    }

    @Override
    public TrashReportResponse createPendingReport(Long citizenId) {
        User citizen = userRepository.findById(citizenId)
                .orElseThrow(() -> new RuntimeException("Citizen not found"));
        
        if (citizen.getWard() == null) {
            throw new RuntimeException("Citizen does not belong to any Ward yet. Please setup your service first.");
        }

        TrashReport report = TrashReport.builder()
                .citizen(citizen)
                .ward(citizen.getWard())
                .status(TrashReportStatus.PENDING)
                .build();
                
        TrashReport saved = trashReportRepository.save(report);
        return mapToResponse(saved);
    }

    @Override
    public List<TrashReportResponse> getCitizenReports(Long citizenId) {
        return trashReportRepository.findAll().stream()
                .filter(r -> r.getCitizen().getId().equals(citizenId))
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<TrashReportResponse> getPendingReportsForReceiver(Long receiverId) {
        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new RuntimeException("Receiver not found"));
                
        if (receiver.getWard() == null) {
            return new ArrayList<>();
        }

        return trashReportRepository.findAll().stream()
                .filter(r -> r.getStatus() == TrashReportStatus.PENDING)
                .filter(r -> r.getWard().getId().equals(receiver.getWard().getId()))
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private TrashReportResponse mapToResponse(TrashReport report) {
        return TrashReportResponse.builder()
                .id(report.getId())
                .citizenName(report.getCitizen().getFullName())
                .wardName(report.getWard().getWardName())
                .status(report.getStatus())
                .totalPointsEarned(report.getTotalPointsEarned())
                .createdAt(report.getCreatedAt())
                .build();
    }
}

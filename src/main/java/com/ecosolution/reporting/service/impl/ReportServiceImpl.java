package com.ecosolution.reporting.service.impl;

import com.ecosolution.reporting.domain.request.ReportRequest;
import com.ecosolution.reporting.domain.response.ReportResponse;
import com.ecosolution.reporting.mapper.impl.ReportMapperImpl;
import com.ecosolution.reporting.repository.WasteReportRepository;
import com.ecosolution.reporting.domain.ReportStatus;
import com.ecosolution.reporting.domain.entity.WasteReport;
import com.ecosolution.core.repository.UserRepository;
import com.ecosolution.core.domain.entity.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
public class ReportServiceImpl implements com.ecosolution.reporting.service.ReportService {

    private final ReportMapperImpl mapper;
    private final WasteReportRepository repo;
    private final UserRepository userRepository;

    public ReportServiceImpl(ReportMapperImpl mapper, WasteReportRepository repo, UserRepository userRepository) {
        this.mapper = mapper;
        this.repo = repo;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public ReportResponse createReport(UUID citizenId, ReportRequest req) {
        // validate citizen
        User citizen = userRepository.findById(citizenId).orElseThrow(() -> new IllegalArgumentException("Citizen not found"));

        // Duplicate prevention BR-07: same ward + address + non-terminal status
        List<ReportStatus> nonTerminal = Arrays.asList(ReportStatus.PENDING, ReportStatus.ACCEPTED, ReportStatus.ASSIGNED);
        var existing = repo.findTopByCreatedByIdAndAddressAndWardIdAndStatusIn(citizenId, req.address(), req.wardId(), nonTerminal);
        if (existing != null && existing.isPresent()) throw new IllegalStateException("Duplicate report exists");

        try {
            WasteReport entity = mapper.toEntity(req);
            entity.setCreatedBy(citizen);
            // status & timestamps handled in entity @PrePersist
            WasteReport saved = repo.save(entity);
            return mapper.toResponse(saved);
        } catch (Exception ex) {
            throw new RuntimeException(ex.getMessage(), ex);
        }
    }
}


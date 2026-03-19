package org.swp391_group4_backend.ecosolution.service;

import org.swp391_group4_backend.ecosolution.dto.response.CollectorStatResponse;
import org.swp391_group4_backend.ecosolution.dto.response.StatsSummaryResponse;

import java.util.List;

public interface AdminService {
    StatsSummaryResponse getSummaryStats();
    List<CollectorStatResponse> getTopCollectors();
}

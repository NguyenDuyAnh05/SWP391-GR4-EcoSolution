package org.swp391_group4_backend.ecosolution.service;

import org.swp391_group4_backend.ecosolution.dto.response.CollectorStatResponse;
import org.swp391_group4_backend.ecosolution.dto.response.StatsSummaryResponse;

import java.util.List;

public interface AdminService {
    StatsSummaryResponse getSummaryStats();
    List<CollectorStatResponse> getTopCollectors();
    
    org.swp391_group4_backend.ecosolution.dto.response.AdminStatsResponse getStats();
    List<org.swp391_group4_backend.ecosolution.dto.response.UserResponse> getCollectors();
    List<org.swp391_group4_backend.ecosolution.dto.response.UserResponse> getReceivers();
    List<org.swp391_group4_backend.ecosolution.dto.response.TransactionResponse> getAllTransactions();
}

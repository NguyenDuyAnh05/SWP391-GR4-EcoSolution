package com.ecosolution.reporting.service;

import com.ecosolution.reporting.domain.request.ReportRequest;
import com.ecosolution.reporting.domain.response.ReportResponse;

import java.util.UUID;

public interface ReportService {
    ReportResponse createReport(UUID citizenId, ReportRequest req);
}


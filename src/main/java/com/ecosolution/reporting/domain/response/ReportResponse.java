package com.ecosolution.reporting.domain.response;

import java.util.UUID;

public record ReportResponse(UUID id, String address, Long wardId, Double quantity, String status) {}


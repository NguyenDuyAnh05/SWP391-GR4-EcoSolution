package org.swp391_group4_backend.ecosolution.reporting.domain.response;

import java.util.UUID;

public record ReportResponse(UUID id, String status, String message) {
}


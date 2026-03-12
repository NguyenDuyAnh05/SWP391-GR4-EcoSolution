package com.ecosolution.reporting.domain.request;

import org.springframework.web.multipart.MultipartFile;

public record ReportRequest(String address, Long wardId, Double quantity, MultipartFile image) {}


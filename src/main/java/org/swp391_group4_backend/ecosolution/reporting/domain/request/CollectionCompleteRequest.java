package org.swp391_group4_backend.ecosolution.reporting.domain.request;

import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public record CollectionCompleteRequest(
        UUID reportId,
        Double actualQuantity,
        MultipartFile proofImage
) {}


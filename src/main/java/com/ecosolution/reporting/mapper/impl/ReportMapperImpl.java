package com.ecosolution.reporting.mapper.impl;

import com.ecosolution.reporting.domain.request.ReportRequest;
import com.ecosolution.reporting.domain.entity.WasteReport;
import com.ecosolution.reporting.domain.response.ReportResponse;
import com.ecosolution.core.repository.WardRepository;
import com.ecosolution.core.domain.entity.Ward;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Optional;

@Component
public class ReportMapperImpl {

    private final WardRepository wardRepository;

    public ReportMapperImpl(WardRepository wardRepository) {
        this.wardRepository = wardRepository;
    }

    public WasteReport toEntity(ReportRequest req) throws IOException {
        if (req == null) throw new IllegalArgumentException("Request cannot be null");
        Optional<Ward> w = wardRepository.findById(req.wardId());
        if (w.isEmpty()) throw new IllegalArgumentException("Invalid wardId");

        byte[] imageBytes = null;
        MultipartFile m = req.image();
        if (m != null && !m.isEmpty()) {
            if (m.getSize() > 5L * 1024L * 1024L) throw new IllegalArgumentException("Image exceeds 5MB limit");
            imageBytes = m.getBytes();
        }

        return WasteReport.builder()
                .address(req.address())
                .ward(w.get())
                .quantity(req.quantity() != null ? BigDecimal.valueOf(req.quantity()) : null)
                .imageData(imageBytes)
                .build();
    }

    public ReportResponse toResponse(WasteReport r) {
        return new ReportResponse(r.getId(), r.getAddress(), r.getWard().getId(), r.getQuantity() != null ? r.getQuantity().doubleValue() : null, r.getStatus().name());
    }
}


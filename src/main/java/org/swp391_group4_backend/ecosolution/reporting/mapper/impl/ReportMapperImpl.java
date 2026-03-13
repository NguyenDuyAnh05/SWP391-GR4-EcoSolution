package org.swp391_group4_backend.ecosolution.reporting.mapper.impl;

import org.swp391_group4_backend.ecosolution.core.domain.entity.Ward;
import org.swp391_group4_backend.ecosolution.core.repository.WardRepository;
import org.swp391_group4_backend.ecosolution.reporting.domain.entity.WasteReport;
import org.swp391_group4_backend.ecosolution.reporting.domain.request.ReportRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@Component
public class ReportMapperImpl {
    public static final long MAX_IMAGE_BYTES = 5L * 1024 * 1024; // 5MB

    private final WardRepository wardRepository;

    public ReportMapperImpl(WardRepository wardRepository) {
        this.wardRepository = wardRepository;
    }

    public WasteReport toEntity(ReportRequest req) throws IOException {
        WasteReport r = new WasteReport();
        r.setAddress(req.address());
        r.setQuantity(req.quantity());
        // description and waste type
        r.setDescription(req.description());
        r.setWasteType(req.wasteType());

        // Ward mapping - reject if wardId provided but not found
        if (req.wardId() != null) {
            Optional<Ward> w = wardRepository.findById(req.wardId());
            if (w.isPresent()) {
                r.setWard(w.get());
            } else {
                throw new IllegalArgumentException("Invalid wardId: " + req.wardId());
            }
        }

        // Multipart -> byte[]
        MultipartFile image = req.image();
        if (image != null && !image.isEmpty()) {
            if (image.getSize() > MAX_IMAGE_BYTES) {
                throw new IllegalArgumentException("Image exceeds maximum allowed size of 5MB");
            }
            r.setImage(image.getBytes());
        }

        return r;
    }
}


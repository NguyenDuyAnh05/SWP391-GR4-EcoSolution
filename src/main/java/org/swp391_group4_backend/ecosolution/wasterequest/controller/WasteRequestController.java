package org.swp391_group4_backend.ecosolution.wasterequest.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.swp391_group4_backend.ecosolution.wasterequest.domain.dto.request.WasteRequestAssignRequestDto;
import org.swp391_group4_backend.ecosolution.wasterequest.domain.dto.request.WasteRequestCompleteRequestDto;
import org.swp391_group4_backend.ecosolution.wasterequest.domain.dto.request.WasteRequestCreateRequestDto;
import org.swp391_group4_backend.ecosolution.wasterequest.domain.dto.response.WasteRequestResponseDto;
import org.swp391_group4_backend.ecosolution.wasterequest.service.WasteRequestService;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/requests")
@RequiredArgsConstructor
public class WasteRequestController {

    private final WasteRequestService wasteRequestService;

    // ============================
    // CITIZEN ENDPOINTS
    // ============================

    /** POST /api/v1/requests — Create a new waste request (CITIZEN) */
    @PostMapping
    public ResponseEntity<WasteRequestResponseDto> createRequest(
            @RequestBody @Valid WasteRequestCreateRequestDto dto,
            @RequestHeader("X-User-Id") UUID userId
    ) {
        WasteRequestResponseDto response = wasteRequestService.createRequest(dto, userId);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /** GET /api/v1/requests/my — List own requests (CITIZEN) */
    @GetMapping("/my")
    public ResponseEntity<List<WasteRequestResponseDto>> getMyRequests(
            @RequestHeader("X-User-Id") UUID userId
    ) {
        List<WasteRequestResponseDto> responses = wasteRequestService.getMyRequests(userId);
        return ResponseEntity.ok(responses);
    }

    /** PATCH /api/v1/requests/{id}/cancel — Cancel a request (CITIZEN) */
    @PatchMapping("/{id}/cancel")
    public ResponseEntity<WasteRequestResponseDto> cancelRequest(
            @PathVariable UUID id,
            @RequestHeader("X-User-Id") UUID userId
    ) {
        WasteRequestResponseDto response = wasteRequestService.cancelRequest(id, userId);
        return ResponseEntity.ok(response);
    }

    // ============================
    // ASSIGNOR ENDPOINTS
    // ============================

    /** GET /api/v1/requests — List all requests (ASSIGNOR) */
    @GetMapping
    public ResponseEntity<List<WasteRequestResponseDto>> getAllRequests(
            @RequestHeader("X-User-Id") UUID userId
    ) {
        List<WasteRequestResponseDto> responses = wasteRequestService.getAllRequests(userId);
        return ResponseEntity.ok(responses);
    }

    /** PATCH /api/v1/requests/{id}/assign — Assign a collector (ASSIGNOR) */
    @PatchMapping("/{id}/assign")
    public ResponseEntity<WasteRequestResponseDto> assignCollector(
            @PathVariable UUID id,
            @RequestBody @Valid WasteRequestAssignRequestDto dto,
            @RequestHeader("X-User-Id") UUID userId
    ) {
        WasteRequestResponseDto response = wasteRequestService.assignCollector(id, dto, userId);
        return ResponseEntity.ok(response);
    }

    // ============================
    // COLLECTOR ENDPOINTS
    // ============================

    /** PATCH /api/v1/requests/{id}/accept — Accept assignment (COLLECTOR) */
    @PatchMapping("/{id}/accept")
    public ResponseEntity<WasteRequestResponseDto> acceptRequest(
            @PathVariable UUID id,
            @RequestHeader("X-User-Id") UUID userId
    ) {
        WasteRequestResponseDto response = wasteRequestService.acceptRequest(id, userId);
        return ResponseEntity.ok(response);
    }

    /** PATCH /api/v1/requests/{id}/start — Mark IN_PROGRESS (COLLECTOR) */
    @PatchMapping("/{id}/start")
    public ResponseEntity<WasteRequestResponseDto> startRequest(
            @PathVariable UUID id,
            @RequestHeader("X-User-Id") UUID userId
    ) {
        WasteRequestResponseDto response = wasteRequestService.startRequest(id, userId);
        return ResponseEntity.ok(response);
    }

    /**
     * PATCH /api/v1/requests/{id}/complete — Mark COMPLETED (COLLECTOR)
     * BR19: Evidence image is required (multipart).
     * BR18: Optional adjustment fields in JSON part.
     */
    @PatchMapping(value = "/{id}/complete", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<WasteRequestResponseDto> completeRequest(
            @PathVariable UUID id,
            @RequestPart("evidenceImage") MultipartFile evidenceImage,
            @RequestPart(value = "adjustment", required = false) @Valid WasteRequestCompleteRequestDto dto,
            @RequestHeader("X-User-Id") UUID userId
    ) throws IOException {
        byte[] imageBytes = evidenceImage.getBytes();
        WasteRequestResponseDto response = wasteRequestService.completeRequest(id, dto, imageBytes, userId);
        return ResponseEntity.ok(response);
    }
}

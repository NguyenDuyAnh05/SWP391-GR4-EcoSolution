package org.swp391_group4_backend.ecosolution.wasterequest.service;

import org.swp391_group4_backend.ecosolution.wasterequest.domain.dto.request.WasteRequestAssignRequestDto;
import org.swp391_group4_backend.ecosolution.wasterequest.domain.dto.request.WasteRequestCompleteRequestDto;
import org.swp391_group4_backend.ecosolution.wasterequest.domain.dto.request.WasteRequestCreateRequestDto;
import org.swp391_group4_backend.ecosolution.wasterequest.domain.dto.response.WasteRequestResponseDto;

import java.util.List;
import java.util.UUID;

public interface WasteRequestService {

    // Citizen actions
    WasteRequestResponseDto createRequest(WasteRequestCreateRequestDto dto, UUID authenticatedCitizenId);
    List<WasteRequestResponseDto> getMyRequests(UUID authenticatedCitizenId);
    WasteRequestResponseDto cancelRequest(UUID requestId, UUID authenticatedCitizenId);

    // Assignor actions
    List<WasteRequestResponseDto> getAllRequests(UUID authenticatedAssignorId);
    WasteRequestResponseDto assignCollector(UUID requestId, WasteRequestAssignRequestDto dto, UUID authenticatedAssignorId);

    // Collector actions
    WasteRequestResponseDto acceptRequest(UUID requestId, UUID authenticatedCollectorId);
    WasteRequestResponseDto startRequest(UUID requestId, UUID authenticatedCollectorId);
    WasteRequestResponseDto completeRequest(UUID requestId, WasteRequestCompleteRequestDto dto, byte[] evidenceImage, UUID authenticatedCollectorId);
}

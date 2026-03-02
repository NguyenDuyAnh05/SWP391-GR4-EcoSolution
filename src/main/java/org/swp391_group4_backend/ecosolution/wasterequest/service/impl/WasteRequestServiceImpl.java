package org.swp391_group4_backend.ecosolution.wasterequest.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.swp391_group4_backend.ecosolution.auth.domain.entity.User;
import org.swp391_group4_backend.ecosolution.auth.domain.entity.UserRole;
import org.swp391_group4_backend.ecosolution.auth.domain.entity.UserStatus;
import org.swp391_group4_backend.ecosolution.auth.repository.UserRepository;
import org.swp391_group4_backend.ecosolution.wasterequest.domain.dto.request.WasteRequestAssignRequestDto;
import org.swp391_group4_backend.ecosolution.wasterequest.domain.dto.request.WasteRequestCompleteRequestDto;
import org.swp391_group4_backend.ecosolution.wasterequest.domain.dto.request.WasteRequestCreateRequestDto;
import org.swp391_group4_backend.ecosolution.wasterequest.domain.dto.response.WasteRequestResponseDto;
import org.swp391_group4_backend.ecosolution.wasterequest.domain.entity.RequestStatus;
import org.swp391_group4_backend.ecosolution.wasterequest.domain.entity.WasteRequest;
import org.swp391_group4_backend.ecosolution.wasterequest.exception.*;
import org.swp391_group4_backend.ecosolution.wasterequest.mapper.WasteRequestMapper;
import org.swp391_group4_backend.ecosolution.wasterequest.repository.WasteRequestRepository;
import org.swp391_group4_backend.ecosolution.wasterequest.service.WasteRequestService;
import org.swp391_group4_backend.ecosolution.wasterequest.statemachine.WasteRequestStateMachine;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WasteRequestServiceImpl implements WasteRequestService {

    private final WasteRequestRepository wasteRequestRepository;
    private final UserRepository userRepository;
    private final WasteRequestMapper wasteRequestMapper;

    private static final long CANCEL_COOLDOWN_MINUTES = 2;

    // ============================
    // CITIZEN ACTIONS
    // ============================

    @Override
    @Transactional
    public WasteRequestResponseDto createRequest(WasteRequestCreateRequestDto dto, UUID authenticatedCitizenId) {
        User citizen = findUserOrThrow(authenticatedCitizenId);

        // Role check
        requireRole(citizen, UserRole.CITIZEN);

        // BR08/BR33: Citizen must be ACTIVE
        requireActiveUser(citizen);

        // BR34: 2-minute cooldown after cancellation
        checkCancelCooldown(authenticatedCitizenId);

        // BR01: Max 1 request per calendar day
        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.atTime(LocalTime.MAX);
        boolean hasRequestToday = wasteRequestRepository.existsByCitizenIdAndCreatedToday(
                authenticatedCitizenId, startOfDay, endOfDay, WasteRequestStateMachine.TERMINAL_STATES
        );
        if (hasRequestToday) {
            throw new DailyRequestLimitExceededException("Maximum 1 request per day");
        }

        // BR07: Duplicate detection (same citizen + address + date + non-terminal)
        boolean duplicate = wasteRequestRepository.existsByCitizenIdAndAddressAndPreferredDateAndStatusNotIn(
                authenticatedCitizenId, dto.address(), dto.preferredDate(), WasteRequestStateMachine.TERMINAL_STATES
        );
        if (duplicate) {
            throw new DuplicateWasteRequestException("A request for this address and date already exists");
        }

        // Map DTO → entity, set citizen, status defaults via @PrePersist
        WasteRequest wasteRequest = wasteRequestMapper.toEntity(dto);
        wasteRequest.setCitizen(citizen);
        wasteRequest.setStatus(RequestStatus.PENDING);

        WasteRequest saved = wasteRequestRepository.save(wasteRequest);
        return wasteRequestMapper.toResponseDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<WasteRequestResponseDto> getMyRequests(UUID authenticatedCitizenId) {
        User citizen = findUserOrThrow(authenticatedCitizenId);
        requireRole(citizen, UserRole.CITIZEN);
        return wasteRequestRepository.findByCitizenIdOrderByCreatedAtDesc(authenticatedCitizenId)
                .stream()
                .map(wasteRequestMapper::toResponseDto)
                .toList();
    }

    @Override
    @Transactional
    public WasteRequestResponseDto cancelRequest(UUID requestId, UUID authenticatedCitizenId) {
        User citizen = findUserOrThrow(authenticatedCitizenId);
        requireRole(citizen, UserRole.CITIZEN);

        WasteRequest request = findRequestOrThrow(requestId);

        // Ownership check
        if (!request.getCitizen().getId().equals(authenticatedCitizenId)) {
            throw new UnauthorizedRoleException("You can only cancel your own requests");
        }

        // BR03/BR21: Cancel only when PENDING
        validateTransition(request, RequestStatus.CANCELLED);

        request.setStatus(RequestStatus.CANCELLED);
        WasteRequest saved = wasteRequestRepository.save(request);
        return wasteRequestMapper.toResponseDto(saved);
    }

    // ============================
    // ASSIGNOR ACTIONS
    // ============================

    @Override
    @Transactional(readOnly = true)
    public List<WasteRequestResponseDto> getAllRequests(UUID authenticatedAssignorId) {
        User assignor = findUserOrThrow(authenticatedAssignorId);
        requireRole(assignor, UserRole.ASSIGNOR);
        return wasteRequestRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(wasteRequestMapper::toResponseDto)
                .toList();
    }

    @Override
    @Transactional
    public WasteRequestResponseDto assignCollector(UUID requestId, WasteRequestAssignRequestDto dto, UUID authenticatedAssignorId) {
        User assignor = findUserOrThrow(authenticatedAssignorId);
        requireRole(assignor, UserRole.ASSIGNOR);

        WasteRequest request = findRequestOrThrow(requestId);

        // BR10: Only PENDING can be assigned
        validateTransition(request, RequestStatus.ASSIGNED);

        // Find the collector
        User collector = userRepository.findById(dto.collectorId())
                .orElseThrow(() -> new UserNotFoundException("Collector not found"));

        // Must be a COLLECTOR role
        if (collector.getRole() != UserRole.COLLECTOR) {
            throw new UnauthorizedRoleException("Target user is not a collector");
        }

        // BR12: Collector must be ACTIVE
        if (collector.getStatus() != UserStatus.ACTIVE) {
            throw new UserSuspendedException("Collector is not active");
        }

        // BR13: One collector per request (assignedCollector is set here only)
        request.setAssignedCollector(collector);
        request.setStatus(RequestStatus.ASSIGNED);

        WasteRequest saved = wasteRequestRepository.save(request);
        return wasteRequestMapper.toResponseDto(saved);
    }

    // ============================
    // COLLECTOR ACTIONS
    // ============================

    @Override
    @Transactional
    public WasteRequestResponseDto acceptRequest(UUID requestId, UUID authenticatedCollectorId) {
        User collector = findUserOrThrow(authenticatedCollectorId);
        requireRole(collector, UserRole.COLLECTOR);
        requireActiveUser(collector);

        WasteRequest request = findRequestOrThrow(requestId);

        // Ownership check: collector must be the assigned one
        requireAssignedCollector(request, authenticatedCollectorId);

        // BR16: Accept only when ASSIGNED
        validateTransition(request, RequestStatus.ACCEPTED);

        request.setStatus(RequestStatus.ACCEPTED);
        WasteRequest saved = wasteRequestRepository.save(request);
        return wasteRequestMapper.toResponseDto(saved);
    }

    @Override
    @Transactional
    public WasteRequestResponseDto startRequest(UUID requestId, UUID authenticatedCollectorId) {
        User collector = findUserOrThrow(authenticatedCollectorId);
        requireRole(collector, UserRole.COLLECTOR);
        requireActiveUser(collector);

        WasteRequest request = findRequestOrThrow(requestId);
        requireAssignedCollector(request, authenticatedCollectorId);

        // BR17: Start → IN_PROGRESS (requires ACCEPTED)
        validateTransition(request, RequestStatus.IN_PROGRESS);

        request.setStatus(RequestStatus.IN_PROGRESS);
        WasteRequest saved = wasteRequestRepository.save(request);
        return wasteRequestMapper.toResponseDto(saved);
    }

    @Override
    @Transactional
    public WasteRequestResponseDto completeRequest(UUID requestId, WasteRequestCompleteRequestDto dto, byte[] evidenceImage, UUID authenticatedCollectorId) {
        User collector = findUserOrThrow(authenticatedCollectorId);
        requireRole(collector, UserRole.COLLECTOR);
        requireActiveUser(collector);

        WasteRequest request = findRequestOrThrow(requestId);
        requireAssignedCollector(request, authenticatedCollectorId);

        // BR20: Complete → COMPLETED (requires IN_PROGRESS)
        validateTransition(request, RequestStatus.COMPLETED);

        // BR19: Evidence image is required
        if (evidenceImage == null || evidenceImage.length == 0) {
            throw new EvidenceRequiredException("Evidence image is required");
        }

        request.setEvidenceImage(evidenceImage);

        // BR18: Collector adjustment — if not provided, auto-copy from original
        if (dto != null && dto.actualWasteType() != null) {
            request.setActualWasteType(dto.actualWasteType());
        } else {
            request.setActualWasteType(request.getWasteType());
        }

        if (dto != null && dto.actualQuantity() != null) {
            request.setActualQuantity(dto.actualQuantity());
        } else {
            request.setActualQuantity(request.getQuantity());
        }

        request.setStatus(RequestStatus.COMPLETED);
        WasteRequest saved = wasteRequestRepository.save(request);
        return wasteRequestMapper.toResponseDto(saved);
    }

    // ============================
    // HELPER METHODS
    // ============================

    private User findUserOrThrow(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
    }

    private WasteRequest findRequestOrThrow(UUID requestId) {
        return wasteRequestRepository.findById(requestId)
                .orElseThrow(() -> new WasteRequestNotFoundException("Waste request not found"));
    }

    private void requireRole(User user, UserRole expectedRole) {
        if (user.getRole() != expectedRole) {
            throw new UnauthorizedRoleException("User does not have the required role: " + expectedRole);
        }
    }

    private void requireActiveUser(User user) {
        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new UserSuspendedException("Your account is suspended");
        }
    }

    /**
     * Validates the transition using the state machine (BR24: strict transitions).
     * Also rejects modifications on terminal states (BR22).
     */
    private void validateTransition(WasteRequest request, RequestStatus targetStatus) {
        if (WasteRequestStateMachine.isTerminal(request.getStatus())) {
            throw new InvalidStateTransitionException("Request is in a terminal state and cannot be modified");
        }
        if (!WasteRequestStateMachine.isValidTransition(request.getStatus(), targetStatus)) {
            throw new InvalidStateTransitionException(
                    String.format("Cannot transition from %s to %s", request.getStatus(), targetStatus)
            );
        }
    }

    /**
     * Checks that the authenticated collector is the one assigned to this request.
     */
    private void requireAssignedCollector(WasteRequest request, UUID collectorId) {
        if (request.getAssignedCollector() == null || !request.getAssignedCollector().getId().equals(collectorId)) {
            throw new UnauthorizedRoleException("You are not the assigned collector");
        }
    }

    /**
     * BR34: After cancelling, citizen must wait 2 minutes before creating a new request.
     */
    private void checkCancelCooldown(UUID citizenId) {
        Optional<WasteRequest> lastCancelled = wasteRequestRepository
                .findTopByCitizenIdAndStatusOrderByUpdatedAtDesc(citizenId, RequestStatus.CANCELLED);

        if (lastCancelled.isPresent()) {
            LocalDateTime cancelledAt = lastCancelled.get().getUpdatedAt();
            LocalDateTime cooldownEnd = cancelledAt.plusMinutes(CANCEL_COOLDOWN_MINUTES);
            if (LocalDateTime.now().isBefore(cooldownEnd)) {
                throw new CancelCooldownException("Please wait before creating a new request");
            }
        }
    }
}

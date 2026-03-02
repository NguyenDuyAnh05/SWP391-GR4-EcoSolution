package org.swp391_group4_backend.ecosolution.wasterequest.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
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
import org.swp391_group4_backend.ecosolution.wasterequest.domain.entity.WasteType;
import org.swp391_group4_backend.ecosolution.wasterequest.exception.*;
import org.swp391_group4_backend.ecosolution.wasterequest.mapper.WasteRequestMapper;
import org.swp391_group4_backend.ecosolution.wasterequest.repository.WasteRequestRepository;
import org.swp391_group4_backend.ecosolution.wasterequest.statemachine.WasteRequestStateMachine;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WasteRequestServiceImplTest {

    @Mock
    private WasteRequestRepository wasteRequestRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private WasteRequestMapper wasteRequestMapper;

    @InjectMocks
    private WasteRequestServiceImpl wasteRequestService;

    private User citizen;
    private UUID citizenId;
    private WasteRequestCreateRequestDto createDto;

    @BeforeEach
    void setUp() {
        citizenId = UUID.randomUUID();
        citizen = User.builder()
                .id(citizenId)
                .name("Test Citizen")
                .role(UserRole.CITIZEN)
                .status(UserStatus.ACTIVE)
                .build();
        createDto = new WasteRequestCreateRequestDto(
                WasteType.RECYCLABLE,
                BigDecimal.valueOf(12.50),
                "123 Street",
                10.0,
                20.0,
                LocalDate.now().plusDays(1)
        );
    }

    @Test
    void createRequest_Success() {
        when(userRepository.findById(citizenId)).thenReturn(Optional.of(citizen));
        when(wasteRequestRepository.findTopByCitizenIdAndStatusOrderByUpdatedAtDesc(any(), any()))
                .thenReturn(Optional.empty());
        when(wasteRequestRepository.existsByCitizenIdAndCreatedToday(any(), any(), any(), any()))
                .thenReturn(false);
        when(wasteRequestRepository.existsByCitizenIdAndAddressAndPreferredDateAndStatusNotIn(any(), any(), any(), any()))
                .thenReturn(false);

        WasteRequest entity = new WasteRequest();
        entity.setWasteType(WasteType.RECYCLABLE);
        entity.setQuantity(BigDecimal.valueOf(12.50));

        when(wasteRequestMapper.toEntity(createDto)).thenReturn(entity);
        when(wasteRequestRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(wasteRequestMapper.toResponseDto(any())).thenReturn(mock(WasteRequestResponseDto.class));

        assertNotNull(wasteRequestService.createRequest(createDto, citizenId));
        verify(wasteRequestRepository).save(any(WasteRequest.class));
        assertEquals(RequestStatus.PENDING, entity.getStatus());
        assertEquals(citizen, entity.getCitizen());
    }

    @Test
    void createRequest_DailyLimitExceeded_ThrowsException() {
        when(userRepository.findById(citizenId)).thenReturn(Optional.of(citizen));
        when(wasteRequestRepository.findTopByCitizenIdAndStatusOrderByUpdatedAtDesc(any(), any()))
                .thenReturn(Optional.empty());
        when(wasteRequestRepository.existsByCitizenIdAndCreatedToday(any(), any(), any(), any()))
                .thenReturn(true);

        assertThrows(DailyRequestLimitExceededException.class, () -> wasteRequestService.createRequest(createDto, citizenId));
    }

    @Test
    void createRequest_Duplicate_ThrowsException() {
        when(userRepository.findById(citizenId)).thenReturn(Optional.of(citizen));
        when(wasteRequestRepository.findTopByCitizenIdAndStatusOrderByUpdatedAtDesc(any(), any()))
                .thenReturn(Optional.empty());
        when(wasteRequestRepository.existsByCitizenIdAndCreatedToday(any(), any(), any(), any()))
                .thenReturn(false);
        when(wasteRequestRepository.existsByCitizenIdAndAddressAndPreferredDateAndStatusNotIn(any(), any(), any(), any()))
                .thenReturn(true);

        assertThrows(DuplicateWasteRequestException.class, () -> wasteRequestService.createRequest(createDto, citizenId));
    }

    @Test
    void createRequest_InvalidRole_ThrowsException() {
        User collector = User.builder()
                .id(citizenId)
                .role(UserRole.COLLECTOR)
                .status(UserStatus.ACTIVE)
                .build();
        when(userRepository.findById(citizenId)).thenReturn(Optional.of(collector));

        assertThrows(UnauthorizedRoleException.class, () -> wasteRequestService.createRequest(createDto, citizenId));
    }

    @Test
    void createRequest_UserSuspended_ThrowsException() {
        citizen.setStatus(UserStatus.BANNED);
        when(userRepository.findById(citizenId)).thenReturn(Optional.of(citizen));

        assertThrows(UserSuspendedException.class, () -> wasteRequestService.createRequest(createDto, citizenId));
    }

    @Test
    void createRequest_CancelCooldown_ThrowsException() {
        WasteRequest lastCancelled = new WasteRequest();
        lastCancelled.setUpdatedAt(LocalDateTime.now().minusMinutes(1));
        when(userRepository.findById(citizenId)).thenReturn(Optional.of(citizen));
        when(wasteRequestRepository.findTopByCitizenIdAndStatusOrderByUpdatedAtDesc(citizenId, RequestStatus.CANCELLED))
                .thenReturn(Optional.of(lastCancelled));

        assertThrows(CancelCooldownException.class, () -> wasteRequestService.createRequest(createDto, citizenId));
    }

    @Test
    void getMyRequests_Success() {
        when(userRepository.findById(citizenId)).thenReturn(Optional.of(citizen));
        when(wasteRequestRepository.findByCitizenIdOrderByCreatedAtDesc(citizenId))
                .thenReturn(List.of(new WasteRequest()));
        when(wasteRequestMapper.toResponseDto(any())).thenReturn(mock(WasteRequestResponseDto.class));

        List<WasteRequestResponseDto> results = wasteRequestService.getMyRequests(citizenId);
        assertFalse(results.isEmpty());
        assertEquals(1, results.size());
    }

    // ============================
    // CANCEL ACTION TESTS
    // ============================

    @Test
    void cancelRequest_Success() {
        WasteRequest request = new WasteRequest();
        request.setCitizen(citizen);
        request.setStatus(RequestStatus.PENDING);
        UUID requestId = UUID.randomUUID();

        when(userRepository.findById(citizenId)).thenReturn(Optional.of(citizen));
        when(wasteRequestRepository.findById(requestId)).thenReturn(Optional.of(request));
        when(wasteRequestRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(wasteRequestMapper.toResponseDto(any())).thenReturn(mock(WasteRequestResponseDto.class));

        WasteRequestResponseDto response = wasteRequestService.cancelRequest(requestId, citizenId);
        assertNotNull(response);
        assertEquals(RequestStatus.CANCELLED, request.getStatus());
    }

    @Test
    void cancelRequest_NotOwner_ThrowsException() {
        WasteRequest request = new WasteRequest();
        User otherCitizen = User.builder().id(UUID.randomUUID()).build();
        request.setCitizen(otherCitizen);
        UUID requestId = UUID.randomUUID();

        when(userRepository.findById(citizenId)).thenReturn(Optional.of(citizen));
        when(wasteRequestRepository.findById(requestId)).thenReturn(Optional.of(request));

        assertThrows(UnauthorizedRoleException.class, () -> wasteRequestService.cancelRequest(requestId, citizenId));
    }

    @Test
    void cancelRequest_NotPending_ThrowsException() {
        WasteRequest request = new WasteRequest();
        request.setCitizen(citizen);
        request.setStatus(RequestStatus.ASSIGNED);
        UUID requestId = UUID.randomUUID();

        when(userRepository.findById(citizenId)).thenReturn(Optional.of(citizen));
        when(wasteRequestRepository.findById(requestId)).thenReturn(Optional.of(request));

        assertThrows(InvalidStateTransitionException.class, () -> wasteRequestService.cancelRequest(requestId, citizenId));
    }

    // ============================
    // ASSIGN ACTION TESTS
    // ============================

    @Test
    void getAllRequests_Success() {
        UUID assignorId = UUID.randomUUID();
        User assignor = User.builder().id(assignorId).role(UserRole.ASSIGNOR).build();

        when(userRepository.findById(assignorId)).thenReturn(Optional.of(assignor));
        when(wasteRequestRepository.findAllByOrderByCreatedAtDesc())
                .thenReturn(List.of(new WasteRequest()));
        when(wasteRequestMapper.toResponseDto(any())).thenReturn(mock(WasteRequestResponseDto.class));

        List<WasteRequestResponseDto> results = wasteRequestService.getAllRequests(assignorId);
        assertFalse(results.isEmpty());
    }

    @Test
    void assignCollector_Success() {
        UUID assignorId = UUID.randomUUID();
        User assignor = User.builder().id(assignorId).role(UserRole.ASSIGNOR).build();
        UUID requestId = UUID.randomUUID();
        WasteRequest request = new WasteRequest();
        request.setStatus(RequestStatus.PENDING);

        UUID collectorId = UUID.randomUUID();
        User collector = User.builder().id(collectorId).role(UserRole.COLLECTOR).status(UserStatus.ACTIVE).build();
        WasteRequestAssignRequestDto assignDto = new WasteRequestAssignRequestDto(collectorId);

        when(userRepository.findById(assignorId)).thenReturn(Optional.of(assignor));
        when(wasteRequestRepository.findById(requestId)).thenReturn(Optional.of(request));
        when(userRepository.findById(collectorId)).thenReturn(Optional.of(collector));
        when(wasteRequestRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(wasteRequestMapper.toResponseDto(any())).thenReturn(mock(WasteRequestResponseDto.class));

        assertNotNull(wasteRequestService.assignCollector(requestId, assignDto, assignorId));
        assertEquals(RequestStatus.ASSIGNED, request.getStatus());
        assertEquals(collector, request.getAssignedCollector());
    }

    @Test
    void assignCollector_CollectorSuspended_ThrowsException() {
        UUID assignorId = UUID.randomUUID();
        User assignor = User.builder().id(assignorId).role(UserRole.ASSIGNOR).build();
        UUID requestId = UUID.randomUUID();
        WasteRequest request = new WasteRequest();
        request.setStatus(RequestStatus.PENDING);

        UUID collectorId = UUID.randomUUID();
        User collector = User.builder().id(collectorId).role(UserRole.COLLECTOR).status(UserStatus.BANNED).build();
        WasteRequestAssignRequestDto assignDto = new WasteRequestAssignRequestDto(collectorId);

        when(userRepository.findById(assignorId)).thenReturn(Optional.of(assignor));
        when(wasteRequestRepository.findById(requestId)).thenReturn(Optional.of(request));
        when(userRepository.findById(collectorId)).thenReturn(Optional.of(collector));

        assertThrows(UserSuspendedException.class, () -> wasteRequestService.assignCollector(requestId, assignDto, assignorId));
    }

    @Test
    void assignCollector_NotAssignor_ThrowsException() {
        UUID notAssignorId = UUID.randomUUID();
        User notAssignor = User.builder().id(notAssignorId).role(UserRole.CITIZEN).build();
        WasteRequestAssignRequestDto assignDto = new WasteRequestAssignRequestDto(UUID.randomUUID());

        when(userRepository.findById(notAssignorId)).thenReturn(Optional.of(notAssignor));

        assertThrows(UnauthorizedRoleException.class, () -> wasteRequestService.assignCollector(UUID.randomUUID(), assignDto, notAssignorId));
    }

    @Test
    void assignCollector_InvalidState_ThrowsException() {
        UUID assignorId = UUID.randomUUID();
        User assignor = User.builder().id(assignorId).role(UserRole.ASSIGNOR).build();
        UUID requestId = UUID.randomUUID();
        WasteRequest request = new WasteRequest();
        request.setStatus(RequestStatus.ACCEPTED); // Not PENDING

        when(userRepository.findById(assignorId)).thenReturn(Optional.of(assignor));
        when(wasteRequestRepository.findById(requestId)).thenReturn(Optional.of(request));

        assertThrows(InvalidStateTransitionException.class, () -> wasteRequestService.assignCollector(requestId, new WasteRequestAssignRequestDto(UUID.randomUUID()), assignorId));
    }

    // ============================
    // COLLECTOR ACTION TESTS
    // ============================

    @Test
    void acceptRequest_Success() {
        UUID collectorId = UUID.randomUUID();
        User collector = User.builder().id(collectorId).role(UserRole.COLLECTOR).status(UserStatus.ACTIVE).build();
        UUID requestId = UUID.randomUUID();
        WasteRequest request = new WasteRequest();
        request.setAssignedCollector(collector);
        request.setStatus(RequestStatus.ASSIGNED);

        when(userRepository.findById(collectorId)).thenReturn(Optional.of(collector));
        when(wasteRequestRepository.findById(requestId)).thenReturn(Optional.of(request));
        when(wasteRequestRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(wasteRequestMapper.toResponseDto(any())).thenReturn(mock(WasteRequestResponseDto.class));

        assertNotNull(wasteRequestService.acceptRequest(requestId, collectorId));
        assertEquals(RequestStatus.ACCEPTED, request.getStatus());
    }

    @Test
    void acceptRequest_NotAssignedCollector_ThrowsException() {
        UUID collectorId = UUID.randomUUID();
        User collector = User.builder().id(collectorId).role(UserRole.COLLECTOR).status(UserStatus.ACTIVE).build();
        UUID otherCollectorId = UUID.randomUUID();
        User otherCollector = User.builder().id(otherCollectorId).build();

        WasteRequest request = new WasteRequest();
        request.setAssignedCollector(otherCollector);
        request.setStatus(RequestStatus.ASSIGNED);

        when(userRepository.findById(collectorId)).thenReturn(Optional.of(collector));
        when(wasteRequestRepository.findById(any())).thenReturn(Optional.of(request));

        assertThrows(UnauthorizedRoleException.class, () -> wasteRequestService.acceptRequest(UUID.randomUUID(), collectorId));
    }

    @Test
    void acceptRequest_InvalidState_ThrowsException() {
        UUID collectorId = UUID.randomUUID();
        User collector = User.builder().id(collectorId).role(UserRole.COLLECTOR).status(UserStatus.ACTIVE).build();
        WasteRequest request = new WasteRequest();
        request.setAssignedCollector(collector);
        request.setStatus(RequestStatus.PENDING); // Not ASSIGNED

        when(userRepository.findById(collectorId)).thenReturn(Optional.of(collector));
        when(wasteRequestRepository.findById(any())).thenReturn(Optional.of(request));

        assertThrows(InvalidStateTransitionException.class, () -> wasteRequestService.acceptRequest(UUID.randomUUID(), collectorId));
    }

    @Test
    void startRequest_Success() {
        UUID collectorId = UUID.randomUUID();
        User collector = User.builder().id(collectorId).role(UserRole.COLLECTOR).status(UserStatus.ACTIVE).build();
        UUID requestId = UUID.randomUUID();
        WasteRequest request = new WasteRequest();
        request.setAssignedCollector(collector);
        request.setStatus(RequestStatus.ACCEPTED);

        when(userRepository.findById(collectorId)).thenReturn(Optional.of(collector));
        when(wasteRequestRepository.findById(requestId)).thenReturn(Optional.of(request));
        when(wasteRequestRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(wasteRequestMapper.toResponseDto(any())).thenReturn(mock(WasteRequestResponseDto.class));

        assertNotNull(wasteRequestService.startRequest(requestId, collectorId));
        assertEquals(RequestStatus.IN_PROGRESS, request.getStatus());
    }

    @Test
    void startRequest_NotAssignedCollector_ThrowsException() {
        UUID collectorId = UUID.randomUUID();
        User collector = User.builder().id(collectorId).role(UserRole.COLLECTOR).status(UserStatus.ACTIVE).build();
        UUID otherId = UUID.randomUUID();
        WasteRequest request = new WasteRequest();
        request.setAssignedCollector(User.builder().id(otherId).build());

        when(userRepository.findById(collectorId)).thenReturn(Optional.of(collector));
        when(wasteRequestRepository.findById(any())).thenReturn(Optional.of(request));

        assertThrows(UnauthorizedRoleException.class, () -> wasteRequestService.startRequest(UUID.randomUUID(), collectorId));
    }

    @Test
    void startRequest_InvalidState_ThrowsException() {
        UUID collectorId = UUID.randomUUID();
        User collector = User.builder().id(collectorId).role(UserRole.COLLECTOR).status(UserStatus.ACTIVE).build();
        WasteRequest request = new WasteRequest();
        request.setAssignedCollector(collector);
        request.setStatus(RequestStatus.ASSIGNED); // Not ACCEPTED

        when(userRepository.findById(collectorId)).thenReturn(Optional.of(collector));
        when(wasteRequestRepository.findById(any())).thenReturn(Optional.of(request));

        assertThrows(InvalidStateTransitionException.class, () -> wasteRequestService.startRequest(UUID.randomUUID(), collectorId));
    }

    @Test
    void completeRequest_Success_WithAdjustment() {
        UUID collectorId = UUID.randomUUID();
        User collector = User.builder().id(collectorId).role(UserRole.COLLECTOR).status(UserStatus.ACTIVE).build();
        UUID requestId = UUID.randomUUID();
        WasteRequest request = new WasteRequest();
        request.setAssignedCollector(collector);
        request.setStatus(RequestStatus.IN_PROGRESS);
        request.setWasteType(WasteType.RECYCLABLE);
        request.setQuantity(BigDecimal.valueOf(10.0));

        WasteRequestCompleteRequestDto completeDto = new WasteRequestCompleteRequestDto(
                WasteType.NON_RECYCLABLE,
                BigDecimal.valueOf(12.0)
        );
        byte[] image = new byte[]{1, 2, 3};

        when(userRepository.findById(collectorId)).thenReturn(Optional.of(collector));
        when(wasteRequestRepository.findById(requestId)).thenReturn(Optional.of(request));
        when(wasteRequestRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(wasteRequestMapper.toResponseDto(any())).thenReturn(mock(WasteRequestResponseDto.class));

        assertNotNull(wasteRequestService.completeRequest(requestId, completeDto, image, collectorId));
        assertEquals(RequestStatus.COMPLETED, request.getStatus());
        assertEquals(WasteType.NON_RECYCLABLE, request.getActualWasteType());
        assertEquals(BigDecimal.valueOf(12.0), request.getActualQuantity());
        assertArrayEquals(image, request.getEvidenceImage());
    }

    @Test
    void completeRequest_Success_AutoCopy() {
        UUID collectorId = UUID.randomUUID();
        User collector = User.builder().id(collectorId).role(UserRole.COLLECTOR).status(UserStatus.ACTIVE).build();
        UUID requestId = UUID.randomUUID();
        WasteRequest request = new WasteRequest();
        request.setAssignedCollector(collector);
        request.setStatus(RequestStatus.IN_PROGRESS);
        request.setWasteType(WasteType.RECYCLABLE);
        request.setQuantity(BigDecimal.valueOf(10.0));

        byte[] image = new byte[]{1, 2, 3};

        when(userRepository.findById(collectorId)).thenReturn(Optional.of(collector));
        when(wasteRequestRepository.findById(requestId)).thenReturn(Optional.of(request));
        when(wasteRequestRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(wasteRequestMapper.toResponseDto(any())).thenReturn(mock(WasteRequestResponseDto.class));

        assertNotNull(wasteRequestService.completeRequest(requestId, null, image, collectorId));
        assertEquals(RequestStatus.COMPLETED, request.getStatus());
        assertEquals(WasteType.RECYCLABLE, request.getActualWasteType());
        assertEquals(BigDecimal.valueOf(10.0), request.getActualQuantity());
    }

    @Test
    void completeRequest_NoEvidence_ThrowsException() {
        UUID collectorId = UUID.randomUUID();
        User collector = User.builder().id(collectorId).role(UserRole.COLLECTOR).status(UserStatus.ACTIVE).build();
        UUID requestId = UUID.randomUUID();
        WasteRequest request = new WasteRequest();
        request.setAssignedCollector(collector);
        request.setStatus(RequestStatus.IN_PROGRESS);

        when(userRepository.findById(collectorId)).thenReturn(Optional.of(collector));
        when(wasteRequestRepository.findById(requestId)).thenReturn(Optional.of(request));

        assertThrows(EvidenceRequiredException.class, () -> wasteRequestService.completeRequest(requestId, null, null, collectorId));
    }


    @Test
    void anyAction_OnTerminalState_ThrowsException() {
        UUID collectorId = UUID.randomUUID();
        User collector = User.builder().id(collectorId).role(UserRole.COLLECTOR).status(UserStatus.ACTIVE).build();
        WasteRequest request = new WasteRequest();
        request.setAssignedCollector(collector);
        request.setStatus(RequestStatus.COMPLETED); // Terminal

        when(userRepository.findById(collectorId)).thenReturn(Optional.of(collector));
        when(wasteRequestRepository.findById(any())).thenReturn(Optional.of(request));

        assertThrows(InvalidStateTransitionException.class, () -> wasteRequestService.acceptRequest(UUID.randomUUID(), collectorId));
    }
}

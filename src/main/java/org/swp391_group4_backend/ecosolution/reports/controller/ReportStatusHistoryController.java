package org.swp391_group4_backend.ecosolution.reports.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.swp391_group4_backend.ecosolution.auth.domain.entity.User;
import org.swp391_group4_backend.ecosolution.auth.exception.UserNotFoundException;
import org.swp391_group4_backend.ecosolution.auth.repository.UserRepository;
import org.swp391_group4_backend.ecosolution.common.domain.dto.response.ErrorResponseDto;
import org.swp391_group4_backend.ecosolution.reports.domain.dto.request.ReportStatusHistoryCreateRequestDto;
import org.swp391_group4_backend.ecosolution.reports.domain.dto.response.ReportStatusHistoryResponseDto;
import org.swp391_group4_backend.ecosolution.reports.domain.entity.ReportStatus;
import org.swp391_group4_backend.ecosolution.reports.domain.entity.ReportStatusHistory;
import org.swp391_group4_backend.ecosolution.reports.domain.entity.WasteReport;
import org.swp391_group4_backend.ecosolution.reports.exception.ReportStatusHistoryNotFoundException;
import org.swp391_group4_backend.ecosolution.reports.exception.WasteReportNotFoundException;
import org.swp391_group4_backend.ecosolution.reports.repository.WasteReportRepository;
import org.swp391_group4_backend.ecosolution.reports.service.ReportStatusHistoryService;

@RestController
@RequestMapping("/api/v1/report-status-history")
@Tag(name = "Report Status History", description = "Audit trail for report status changes")
public class ReportStatusHistoryController {

  private final ReportStatusHistoryService reportStatusHistoryService;
  private final WasteReportRepository wasteReportRepository;
  private final UserRepository userRepository;

  public ReportStatusHistoryController(
      ReportStatusHistoryService reportStatusHistoryService,
      WasteReportRepository wasteReportRepository,
      UserRepository userRepository
  ) {
    this.reportStatusHistoryService = reportStatusHistoryService;
    this.wasteReportRepository = wasteReportRepository;
    this.userRepository = userRepository;
  }

  @PostMapping
  @Transactional
  @PreAuthorize("hasAnyRole('ASSIGNOR','ENTERPRISE_ADMIN','SYSTEM_ADMIN')")
  @Operation(
      summary = "Create status history",
      description = "Record a status transition for a report",
      security = @SecurityRequirement(name = "bearerAuth")
  )
  @ApiResponses({
      @ApiResponse(responseCode = "201", description = "History entry created", content = @Content(
          schema = @Schema(implementation = ReportStatusHistoryResponseDto.class)
      )),
      @ApiResponse(responseCode = "400", description = "Invalid request", content = @Content(
          schema = @Schema(implementation = ErrorResponseDto.class)
      )),
      @ApiResponse(responseCode = "404", description = "Report or user not found", content = @Content(
          schema = @Schema(implementation = ErrorResponseDto.class)
      ))
  })
  public ResponseEntity<ReportStatusHistoryResponseDto> createHistory(
      @RequestBody @Valid ReportStatusHistoryCreateRequestDto requestDto
  ) {
    WasteReport report = wasteReportRepository.findById(requestDto.reportId())
        .orElseThrow(() -> new WasteReportNotFoundException(requestDto.reportId()));

    User changedBy = userRepository.findById(requestDto.changedById())
        .orElseThrow(() -> new UserNotFoundException(requestDto.changedById()));

    ReportStatusHistory history = ReportStatusHistory.builder()
        .report(report)
        .statusFrom(requestDto.statusFrom())
        .statusTo(requestDto.statusTo())
        .changedBy(changedBy)
        .reason(requestDto.reason())
        .changedAt(LocalDateTime.now())
        .build();

    ReportStatusHistory savedHistory = reportStatusHistoryService.create(history);
    return new ResponseEntity<>(toResponse(savedHistory), HttpStatus.CREATED);
  }

  @GetMapping("/{historyId}")
  @Transactional(readOnly = true)
  @PreAuthorize("hasAnyRole('ASSIGNOR','ENTERPRISE_ADMIN','SYSTEM_ADMIN')")
  @Operation(
      summary = "Get history entry by id",
      description = "Retrieve a status history entry by identifier",
      security = @SecurityRequirement(name = "bearerAuth")
  )
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "History found", content = @Content(
          schema = @Schema(implementation = ReportStatusHistoryResponseDto.class)
      )),
      @ApiResponse(responseCode = "404", description = "History not found", content = @Content(
          schema = @Schema(implementation = ErrorResponseDto.class)
      ))
  })
  public ResponseEntity<ReportStatusHistoryResponseDto> getHistoryById(
      @PathVariable UUID historyId
  ) {
    ReportStatusHistory history = reportStatusHistoryService.getById(historyId)
        .orElseThrow(() -> new ReportStatusHistoryNotFoundException(historyId));
    return ResponseEntity.ok(toResponse(history));
  }

  @GetMapping
  @Transactional(readOnly = true)
  @PreAuthorize("hasAnyRole('ASSIGNOR','ENTERPRISE_ADMIN','SYSTEM_ADMIN')")
  @Operation(
      summary = "Get status history",
      description = "Get all status history entries, optionally filtered by report",
      security = @SecurityRequirement(name = "bearerAuth")
  )
  public ResponseEntity<List<ReportStatusHistoryResponseDto>> getHistory(
      @Parameter(description = "Filter by report ID")
      @RequestParam(required = false) UUID reportId,
      @Parameter(description = "Filter by status from")
      @RequestParam(required = false) ReportStatus statusFrom,
      @Parameter(description = "Filter by status to")
      @RequestParam(required = false) ReportStatus statusTo
  ) {
    List<ReportStatusHistory> histories = reportStatusHistoryService.getAll();

    if (reportId != null) {
      histories = histories.stream()
          .filter(h -> h.getReport().getId().equals(reportId))
          .collect(Collectors.toList());
    }
    if (statusFrom != null) {
      histories = histories.stream()
          .filter(h -> h.getStatusFrom() == statusFrom)
          .collect(Collectors.toList());
    }
    if (statusTo != null) {
      histories = histories.stream()
          .filter(h -> h.getStatusTo() == statusTo)
          .collect(Collectors.toList());
    }

    List<ReportStatusHistoryResponseDto> responseDtos = histories.stream()
        .map(this::toResponse)
        .collect(Collectors.toList());

    return ResponseEntity.ok(responseDtos);
  }

  private ReportStatusHistoryResponseDto toResponse(ReportStatusHistory history) {
    return new ReportStatusHistoryResponseDto(
        history.getId(),
        history.getReport().getId(),
        history.getStatusFrom(),
        history.getStatusTo(),
        history.getChangedBy().getId(),
        history.getChangedBy().getName(),
        history.getReason(),
        history.getChangedAt()
    );
  }
}


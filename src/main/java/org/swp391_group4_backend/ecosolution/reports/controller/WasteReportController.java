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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.swp391_group4_backend.ecosolution.auth.domain.entity.User;
import org.swp391_group4_backend.ecosolution.auth.repository.UserRepository;
import org.swp391_group4_backend.ecosolution.common.domain.dto.response.ErrorResponseDto;
import org.swp391_group4_backend.ecosolution.reports.domain.dto.request.WasteReportCreateRequestDto;
import org.swp391_group4_backend.ecosolution.reports.domain.dto.request.WasteReportVerifyRequestDto;
import org.swp391_group4_backend.ecosolution.reports.domain.dto.response.WasteReportResponseDto;
import org.swp391_group4_backend.ecosolution.reports.domain.entity.ReportStatus;
import org.swp391_group4_backend.ecosolution.reports.domain.entity.WasteReport;
import org.swp391_group4_backend.ecosolution.reports.service.WasteReportService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/reports")
@Tag(name = "Waste Reports", description = "Citizen waste report submission and management")
public class WasteReportController {

  private final WasteReportService wasteReportService;
  private final UserRepository userRepository;

  public WasteReportController(WasteReportService wasteReportService, UserRepository userRepository) {
    this.wasteReportService = wasteReportService;
    this.userRepository = userRepository;
  }

  @PostMapping
  @Transactional
  @PreAuthorize("hasAnyRole('CITIZEN', 'SYSTEM_ADMIN', 'ENTERPRISE_ADMIN')")
  @Operation(
      summary = "Create waste report",
      description = "Citizens can submit a waste report with location and details",
      security = @SecurityRequirement(name = "bearerAuth")
  )
  @ApiResponses({
      @ApiResponse(responseCode = "201", description = "Report created successfully", content = @Content(
          schema = @Schema(implementation = WasteReportResponseDto.class)
      )),
      @ApiResponse(responseCode = "400", description = "Invalid request data", content = @Content(
          schema = @Schema(implementation = ErrorResponseDto.class)
      )),
      @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(
          schema = @Schema(implementation = ErrorResponseDto.class)
      )),
      @ApiResponse(responseCode = "403", description = "Access denied - Citizens only", content = @Content(
          schema = @Schema(implementation = ErrorResponseDto.class)
      ))
  })
  public ResponseEntity<WasteReportResponseDto> createReport(
      @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Waste report details", required = true)
      @org.springframework.web.bind.annotation.RequestBody @Valid WasteReportCreateRequestDto requestDto,
      @Parameter(description = "Citizen User ID", required = true, example = "a0b1c2d3-e4f5-6789-abcd-ef0123456789")
      @RequestParam UUID citizenId
  ) {
    User citizen = userRepository.findById(citizenId)
        .orElseThrow(() -> new RuntimeException("Citizen not found"));

    WasteReport report = WasteReport.builder()
        .citizen(citizen)
        .declaredWeight(requestDto.declaredWeight())
        .description(requestDto.description())
        .latitude(requestDto.latitude())
        .longitude(requestDto.longitude())
        .currentStatus(ReportStatus.PENDING)
        .createdAt(LocalDateTime.now())
        .slaDeadlineAt(LocalDateTime.now().plusHours(48)) // 48-hour SLA
        .build();

    WasteReport savedReport = wasteReportService.create(report);
    return new ResponseEntity<>(toResponseDto(savedReport), HttpStatus.CREATED);
  }

  @GetMapping("/{reportId}")
  @Transactional(readOnly = true)
  @PreAuthorize("hasAnyRole('CITIZEN', 'COLLECTOR', 'ASSIGNOR', 'ENTERPRISE_ADMIN', 'SYSTEM_ADMIN')")
  @Operation(
      summary = "Get report by ID",
      description = "Retrieves a waste report by its unique identifier",
      security = @SecurityRequirement(name = "bearerAuth")
  )
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Report found", content = @Content(
          schema = @Schema(implementation = WasteReportResponseDto.class)
      )),
      @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(
          schema = @Schema(implementation = ErrorResponseDto.class)
      )),
      @ApiResponse(responseCode = "404", description = "Report not found", content = @Content(
          schema = @Schema(implementation = ErrorResponseDto.class)
      ))
  })
  public ResponseEntity<WasteReportResponseDto> getReportById(
      @Parameter(description = "Report ID", required = true, example = "a0b1c2d3-e4f5-6789-abcd-ef0123456789")
      @PathVariable UUID reportId
  ) {
    WasteReport report = wasteReportService.getById(reportId)
        .orElseThrow(() -> new RuntimeException("Waste report not found: " + reportId));
    return ResponseEntity.ok(toResponseDto(report));
  }

  @GetMapping
  @Transactional(readOnly = true)
  @PreAuthorize("hasAnyRole('CITIZEN', 'COLLECTOR', 'ASSIGNOR', 'ENTERPRISE_ADMIN', 'SYSTEM_ADMIN')")
  @Operation(
      summary = "Get all waste reports",
      description = "Retrieves all waste reports, optionally filtered by status and citizen ID",
      security = @SecurityRequirement(name = "bearerAuth")
  )
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Reports retrieved successfully"),
      @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(
          schema = @Schema(implementation = ErrorResponseDto.class)
      ))
  })
  public ResponseEntity<List<WasteReportResponseDto>> getAllReports(
      @Parameter(description = "Filter by status", example = "PENDING")
      @RequestParam(required = false) ReportStatus status,
      @Parameter(description = "Filter by citizen ID (for citizens to view their own reports)")
      @RequestParam(required = false) UUID citizenId
  ) {
    List<WasteReport> reports = wasteReportService.getAll();

    // Filter by citizen ID if provided
    if (citizenId != null) {
      reports = reports.stream()
          .filter(r -> r.getCitizen().getId().equals(citizenId))
          .collect(Collectors.toList());
    }

    // Filter by status if provided
    if (status != null) {
      reports = reports.stream()
          .filter(r -> r.getCurrentStatus() == status)
          .collect(Collectors.toList());
    }

    List<WasteReportResponseDto> responseDtos = reports.stream()
        .map(this::toResponseDto)
        .collect(Collectors.toList());

    return ResponseEntity.ok(responseDtos);
  }

  @PatchMapping("/{reportId}/verify")
  @Transactional
  @PreAuthorize("hasAnyRole('COLLECTOR', 'ASSIGNOR', 'ENTERPRISE_ADMIN', 'SYSTEM_ADMIN')")
  @Operation(
      summary = "Verify waste report",
      description = "Updates the verified weight after collection and marks report as verified",
      security = @SecurityRequirement(name = "bearerAuth")
  )
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Report verified successfully", content = @Content(
          schema = @Schema(implementation = WasteReportResponseDto.class)
      )),
      @ApiResponse(responseCode = "400", description = "Invalid verification data", content = @Content(
          schema = @Schema(implementation = ErrorResponseDto.class)
      )),
      @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(
          schema = @Schema(implementation = ErrorResponseDto.class)
      )),
      @ApiResponse(responseCode = "404", description = "Report not found", content = @Content(
          schema = @Schema(implementation = ErrorResponseDto.class)
      ))
  })
  public ResponseEntity<WasteReportResponseDto> verifyReport(
      @Parameter(description = "Report ID", required = true)
      @PathVariable UUID reportId,
      @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Verification details", required = true)
      @org.springframework.web.bind.annotation.RequestBody @Valid WasteReportVerifyRequestDto requestDto
  ) {
    WasteReport report = wasteReportService.getById(reportId)
        .orElseThrow(() -> new RuntimeException("Waste report not found: " + reportId));

    report.setVerifiedWeight(requestDto.verifiedWeight());
    report.setCurrentStatus(ReportStatus.VERIFIED);

    WasteReport updatedReport = wasteReportService.update(reportId, report);
    return ResponseEntity.ok(toResponseDto(updatedReport));
  }

  @PatchMapping("/{reportId}/status")
  @Transactional
  @PreAuthorize("hasAnyRole('ASSIGNOR', 'ENTERPRISE_ADMIN', 'SYSTEM_ADMIN')")
  @Operation(
      summary = "Update report status",
      description = "Updates the status of a waste report (PENDING, ASSIGNED, IN_PROGRESS, COMPLETED, CANCELLED)",
      security = @SecurityRequirement(name = "bearerAuth")
  )
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Status updated successfully", content = @Content(
          schema = @Schema(implementation = WasteReportResponseDto.class)
      )),
      @ApiResponse(responseCode = "400", description = "Invalid status transition", content = @Content(
          schema = @Schema(implementation = ErrorResponseDto.class)
      )),
      @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(
          schema = @Schema(implementation = ErrorResponseDto.class)
      )),
      @ApiResponse(responseCode = "404", description = "Report not found", content = @Content(
          schema = @Schema(implementation = ErrorResponseDto.class)
      ))
  })
  public ResponseEntity<WasteReportResponseDto> updateReportStatus(
      @Parameter(description = "Report ID", required = true)
      @PathVariable UUID reportId,
      @Parameter(description = "New status", required = true, example = "ASSIGNED")
      @RequestParam ReportStatus status
  ) {
    WasteReport report = wasteReportService.getById(reportId)
        .orElseThrow(() -> new RuntimeException("Waste report not found: " + reportId));

    report.setCurrentStatus(status);
    WasteReport updatedReport = wasteReportService.update(reportId, report);
    return ResponseEntity.ok(toResponseDto(updatedReport));
  }

  @DeleteMapping("/{reportId}")
  @Transactional
  @PreAuthorize("hasAnyRole('SYSTEM_ADMIN', 'ENTERPRISE_ADMIN')")
  @Operation(
      summary = "Delete waste report",
      description = "Permanently deletes a waste report (admin only)",
      security = @SecurityRequirement(name = "bearerAuth")
  )
  @ApiResponses({
      @ApiResponse(responseCode = "204", description = "Report deleted successfully"),
      @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(
          schema = @Schema(implementation = ErrorResponseDto.class)
      )),
      @ApiResponse(responseCode = "403", description = "Access denied - Admins only", content = @Content(
          schema = @Schema(implementation = ErrorResponseDto.class)
      )),
      @ApiResponse(responseCode = "404", description = "Report not found", content = @Content(
          schema = @Schema(implementation = ErrorResponseDto.class)
      ))
  })
  public ResponseEntity<Void> deleteReport(
      @Parameter(description = "Report ID", required = true)
      @PathVariable UUID reportId
  ) {
    if (wasteReportService.getById(reportId).isEmpty()) {
      throw new RuntimeException("Waste report not found: " + reportId);
    }
    wasteReportService.delete(reportId);
    return ResponseEntity.noContent().build();
  }

  private WasteReportResponseDto toResponseDto(WasteReport report) {
    return new WasteReportResponseDto(
        report.getId(),
        report.getCitizen().getId(),
        report.getCitizen().getName(),
        report.getDeclaredWeight(),
        report.getVerifiedWeight(),
        report.getDescription(),
        report.getLatitude(),
        report.getLongitude(),
        report.getCurrentStatus(),
        report.getCancelReasonCode(),
        report.getSlaDeadlineAt(),
        report.getCreatedAt()
    );
  }
}









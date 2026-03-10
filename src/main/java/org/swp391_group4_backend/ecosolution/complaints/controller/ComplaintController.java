package org.swp391_group4_backend.ecosolution.complaints.controller;

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
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.swp391_group4_backend.ecosolution.auth.domain.entity.User;
import org.swp391_group4_backend.ecosolution.auth.domain.entity.UserAuth;
import org.swp391_group4_backend.ecosolution.auth.repository.UserAuthRepository;
import org.swp391_group4_backend.ecosolution.common.domain.dto.response.ErrorResponseDto;
import org.swp391_group4_backend.ecosolution.complaints.domain.dto.request.ComplaintCreateRequestDto;
import org.swp391_group4_backend.ecosolution.complaints.domain.dto.response.ComplaintResponseDto;
import org.swp391_group4_backend.ecosolution.complaints.domain.entity.Complaint;
import org.swp391_group4_backend.ecosolution.complaints.domain.entity.ComplaintStatus;
import org.swp391_group4_backend.ecosolution.complaints.domain.entity.ComplaintType;
import org.swp391_group4_backend.ecosolution.complaints.exception.ComplaintNotFoundException;
import org.swp391_group4_backend.ecosolution.complaints.service.ComplaintService;
import org.swp391_group4_backend.ecosolution.reports.domain.entity.WasteReport;
import org.swp391_group4_backend.ecosolution.reports.exception.WasteReportNotFoundException;
import org.swp391_group4_backend.ecosolution.reports.service.WasteReportService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/complaints")
@Tag(name = "Complaints", description = "Complaint management for waste reports")
public class ComplaintController {
    private final ComplaintService complaintService;
    private final WasteReportService wasteReportService;
    private final UserAuthRepository userAuthRepository;

    public ComplaintController(ComplaintService complaintService,
                                WasteReportService wasteReportService,
                                UserAuthRepository userAuthRepository) {
        this.complaintService = complaintService;
        this.wasteReportService = wasteReportService;
        this.userAuthRepository = userAuthRepository;
    }

    @PostMapping
    @Transactional
    @PreAuthorize("hasAnyRole('CITIZEN','ENTERPRISE_ADMIN','SYSTEM_ADMIN')")
    @Operation(
            summary = "Create a complaint",
            description = "Creates a new complaint for a waste report",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Complaint created", content = @Content(
                    schema = @Schema(implementation = ComplaintResponseDto.class)
            )),
            @ApiResponse(responseCode = "400", description = "Invalid request", content = @Content(
                    schema = @Schema(implementation = ErrorResponseDto.class)
            )),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(
                    schema = @Schema(implementation = ErrorResponseDto.class)
            )),
            @ApiResponse(responseCode = "404", description = "Report not found", content = @Content(
                    schema = @Schema(implementation = ErrorResponseDto.class)
            ))
    })
    public ResponseEntity<ComplaintResponseDto> createComplaint(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Complaint creation payload", required = true)
            @RequestBody @Valid ComplaintCreateRequestDto requestDto,
            Authentication authentication
    ) {
        String username = authentication.getName();
        UserAuth userAuth = userAuthRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        User citizen = userAuth.getUser();

        WasteReport report = wasteReportService.getById(requestDto.reportId())
                .orElseThrow(() -> new WasteReportNotFoundException(requestDto.reportId()));

        Complaint complaint = Complaint.builder()
                .report(report)
                .citizen(citizen)
                .type(requestDto.type())
                .status(ComplaintStatus.OPEN)
                .description(requestDto.description())
                .createdAt(LocalDateTime.now())
                .build();

        Complaint createdComplaint = complaintService.create(complaint);
        return new ResponseEntity<>(toResponseDto(createdComplaint), HttpStatus.CREATED);
    }

    @GetMapping("/{complaintId}")
    @Transactional(readOnly = true)
    @PreAuthorize("hasAnyRole('CITIZEN','ENTERPRISE_ADMIN','SYSTEM_ADMIN')")
    @Operation(
            summary = "Get complaint by ID",
            description = "Returns a complaint detail by complaint identifier",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Complaint found", content = @Content(
                    schema = @Schema(implementation = ComplaintResponseDto.class)
            )),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(
                    schema = @Schema(implementation = ErrorResponseDto.class)
            )),
            @ApiResponse(responseCode = "404", description = "Complaint not found", content = @Content(
                    schema = @Schema(implementation = ErrorResponseDto.class)
            ))
    })
    public ResponseEntity<ComplaintResponseDto> getComplaintById(
            @Parameter(description = "Complaint ID", required = true, example = "a0b1c2d3-e4f5-6789-abcd-ef0123456789")
            @PathVariable UUID complaintId
    ) {
        Complaint complaint = complaintService.getById(complaintId)
                .orElseThrow(() -> new ComplaintNotFoundException(complaintId));
        return ResponseEntity.ok(toResponseDto(complaint));
    }

    @GetMapping
    @Transactional(readOnly = true)
    @PreAuthorize("hasAnyRole('CITIZEN','ENTERPRISE_ADMIN','SYSTEM_ADMIN')")
    @Operation(
            summary = "Get all complaints",
            description = "Returns all complaints with optional filters",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Complaints retrieved", content = @Content(
                    schema = @Schema(implementation = ComplaintResponseDto.class)
            )),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(
                    schema = @Schema(implementation = ErrorResponseDto.class)
            ))
    })
    public ResponseEntity<List<ComplaintResponseDto>> getAllComplaints(
            @Parameter(description = "Filter by complaint type", example = "NOT_COLLECTED")
            @RequestParam(required = false) ComplaintType type,
            @Parameter(description = "Filter by complaint status", example = "OPEN")
            @RequestParam(required = false) ComplaintStatus status
    ) {
        List<ComplaintResponseDto> response = complaintService.getAll()
                .stream()
                .filter(c -> type == null || c.getType().equals(type))
                .filter(c -> status == null || c.getStatus().equals(status))
                .map(this::toResponseDto)
                .toList();
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{complaintId}/status")
    @Transactional
    @PreAuthorize("hasAnyRole('ENTERPRISE_ADMIN','SYSTEM_ADMIN')")
    @Operation(
            summary = "Update complaint status",
            description = "Updates the status of a complaint",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Complaint status updated", content = @Content(
                    schema = @Schema(implementation = ComplaintResponseDto.class)
            )),
            @ApiResponse(responseCode = "400", description = "Invalid status", content = @Content(
                    schema = @Schema(implementation = ErrorResponseDto.class)
            )),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(
                    schema = @Schema(implementation = ErrorResponseDto.class)
            )),
            @ApiResponse(responseCode = "404", description = "Complaint not found", content = @Content(
                    schema = @Schema(implementation = ErrorResponseDto.class)
            ))
    })
    public ResponseEntity<ComplaintResponseDto> updateComplaintStatus(
            @Parameter(description = "Complaint ID", required = true, example = "a0b1c2d3-e4f5-6789-abcd-ef0123456789")
            @PathVariable UUID complaintId,
            @Parameter(description = "New status", required = true, example = "RESOLVED")
            @RequestParam ComplaintStatus status
    ) {
        Complaint complaint = complaintService.getById(complaintId)
                .orElseThrow(() -> new ComplaintNotFoundException(complaintId));

        complaint.setStatus(status);
        if (status == ComplaintStatus.RESOLVED || status == ComplaintStatus.REJECTED) {
            complaint.setResolvedAt(LocalDateTime.now());
        }

        Complaint updatedComplaint = complaintService.update(complaintId, complaint);
        return ResponseEntity.ok(toResponseDto(updatedComplaint));
    }

    @DeleteMapping("/{complaintId}")
    @Transactional
    @PreAuthorize("hasAnyRole('ENTERPRISE_ADMIN','SYSTEM_ADMIN')")
    @Operation(
            summary = "Delete complaint",
            description = "Deletes a complaint by ID",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Complaint deleted"),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(
                    schema = @Schema(implementation = ErrorResponseDto.class)
            )),
            @ApiResponse(responseCode = "404", description = "Complaint not found", content = @Content(
                    schema = @Schema(implementation = ErrorResponseDto.class)
            ))
    })
    public ResponseEntity<Void> deleteComplaint(
            @Parameter(description = "Complaint ID", required = true, example = "a0b1c2d3-e4f5-6789-abcd-ef0123456789")
            @PathVariable UUID complaintId
    ) {
        complaintService.delete(complaintId);
        return ResponseEntity.noContent().build();
    }

    private ComplaintResponseDto toResponseDto(Complaint complaint) {
        return new ComplaintResponseDto(
                complaint.getId(),
                complaint.getReport().getId(),
                complaint.getCitizen().getId(),
                complaint.getCitizen().getName(),
                complaint.getType(),
                complaint.getStatus(),
                complaint.getDescription(),
                complaint.getCreatedAt(),
                complaint.getResolvedAt()
        );
    }
}





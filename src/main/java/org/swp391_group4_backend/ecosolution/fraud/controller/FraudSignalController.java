package org.swp391_group4_backend.ecosolution.fraud.controller;

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
import org.swp391_group4_backend.ecosolution.auth.repository.UserRepository;
import org.swp391_group4_backend.ecosolution.common.domain.dto.response.ErrorResponseDto;
import org.swp391_group4_backend.ecosolution.fraud.domain.dto.request.FraudSignalCreateRequestDto;
import org.swp391_group4_backend.ecosolution.fraud.domain.dto.response.FraudSignalResponseDto;
import org.swp391_group4_backend.ecosolution.fraud.domain.entity.FraudSignal;
import org.swp391_group4_backend.ecosolution.fraud.domain.entity.FraudType;
import org.swp391_group4_backend.ecosolution.fraud.exception.FraudSignalNotFoundException;
import org.swp391_group4_backend.ecosolution.fraud.service.FraudSignalService;
import org.swp391_group4_backend.ecosolution.reports.domain.entity.WasteReport;
import org.swp391_group4_backend.ecosolution.reports.exception.WasteReportNotFoundException;
import org.swp391_group4_backend.ecosolution.reports.service.WasteReportService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/fraud-signals")
@Tag(name = "Fraud Signals", description = "Fraud signal detection and management")
public class FraudSignalController {
    private final FraudSignalService fraudSignalService;
    private final UserRepository userRepository;
    private final WasteReportService wasteReportService;

    public FraudSignalController(FraudSignalService fraudSignalService,
                                  UserRepository userRepository,
                                  WasteReportService wasteReportService) {
        this.fraudSignalService = fraudSignalService;
        this.userRepository = userRepository;
        this.wasteReportService = wasteReportService;
    }

    @PostMapping
    @Transactional
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN','ENTERPRISE_ADMIN')")
    @Operation(
            summary = "Create a fraud signal",
            description = "Creates a new fraud signal for monitoring suspicious activities",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Fraud signal created", content = @Content(
                    schema = @Schema(implementation = FraudSignalResponseDto.class)
            )),
            @ApiResponse(responseCode = "400", description = "Invalid request", content = @Content(
                    schema = @Schema(implementation = ErrorResponseDto.class)
            )),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(
                    schema = @Schema(implementation = ErrorResponseDto.class)
            )),
            @ApiResponse(responseCode = "404", description = "Citizen or Report not found", content = @Content(
                    schema = @Schema(implementation = ErrorResponseDto.class)
            ))
    })
    public ResponseEntity<FraudSignalResponseDto> createFraudSignal(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Fraud signal creation payload", required = true)
            @RequestBody @Valid FraudSignalCreateRequestDto requestDto
    ) {
        User citizen = userRepository.findById(requestDto.citizenId())
                .orElseThrow(() -> new RuntimeException("Citizen not found with ID: " + requestDto.citizenId()));

        WasteReport report = wasteReportService.getById(requestDto.reportId())
                .orElseThrow(() -> new WasteReportNotFoundException(requestDto.reportId()));

        FraudSignal fraudSignal = FraudSignal.builder()
                .citizen(citizen)
                .report(report)
                .type(requestDto.type())
                .score(requestDto.score())
                .createdAt(LocalDateTime.now())
                .build();

        FraudSignal createdSignal = fraudSignalService.create(fraudSignal);
        return new ResponseEntity<>(toResponseDto(createdSignal), HttpStatus.CREATED);
    }

    @GetMapping("/{signalId}")
    @Transactional(readOnly = true)
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN','ENTERPRISE_ADMIN')")
    @Operation(
            summary = "Get fraud signal by ID",
            description = "Returns a fraud signal detail by signal identifier",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Fraud signal found", content = @Content(
                    schema = @Schema(implementation = FraudSignalResponseDto.class)
            )),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(
                    schema = @Schema(implementation = ErrorResponseDto.class)
            )),
            @ApiResponse(responseCode = "404", description = "Fraud signal not found", content = @Content(
                    schema = @Schema(implementation = ErrorResponseDto.class)
            ))
    })
    public ResponseEntity<FraudSignalResponseDto> getFraudSignalById(
            @Parameter(description = "Fraud Signal ID", required = true, example = "a0b1c2d3-e4f5-6789-abcd-ef0123456789")
            @PathVariable UUID signalId
    ) {
        FraudSignal fraudSignal = fraudSignalService.getById(signalId)
                .orElseThrow(() -> new FraudSignalNotFoundException(signalId));
        return ResponseEntity.ok(toResponseDto(fraudSignal));
    }

    @GetMapping
    @Transactional(readOnly = true)
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN','ENTERPRISE_ADMIN')")
    @Operation(
            summary = "Get all fraud signals",
            description = "Returns all fraud signals with optional filters",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Fraud signals retrieved", content = @Content(
                    schema = @Schema(implementation = FraudSignalResponseDto.class)
            )),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(
                    schema = @Schema(implementation = ErrorResponseDto.class)
            ))
    })
    public ResponseEntity<List<FraudSignalResponseDto>> getAllFraudSignals(
            @Parameter(description = "Filter by fraud type", example = "WEIGHT_MANIPULATION")
            @RequestParam(required = false) FraudType type,
            @Parameter(description = "Filter by minimum score", example = "50")
            @RequestParam(required = false) Integer minScore
    ) {
        List<FraudSignalResponseDto> response = fraudSignalService.getAll()
                .stream()
                .filter(signal -> type == null || signal.getType().equals(type))
                .filter(signal -> minScore == null || signal.getScore() >= minScore)
                .map(this::toResponseDto)
                .toList();
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{signalId}")
    @Transactional
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN','ENTERPRISE_ADMIN')")
    @Operation(
            summary = "Update fraud signal",
            description = "Updates a fraud signal",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Fraud signal updated", content = @Content(
                    schema = @Schema(implementation = FraudSignalResponseDto.class)
            )),
            @ApiResponse(responseCode = "400", description = "Invalid request", content = @Content(
                    schema = @Schema(implementation = ErrorResponseDto.class)
            )),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(
                    schema = @Schema(implementation = ErrorResponseDto.class)
            )),
            @ApiResponse(responseCode = "404", description = "Fraud signal not found", content = @Content(
                    schema = @Schema(implementation = ErrorResponseDto.class)
            ))
    })
    public ResponseEntity<FraudSignalResponseDto> updateFraudSignal(
            @Parameter(description = "Fraud Signal ID", required = true, example = "a0b1c2d3-e4f5-6789-abcd-ef0123456789")
            @PathVariable UUID signalId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Fraud signal update payload", required = true)
            @RequestBody @Valid FraudSignalCreateRequestDto requestDto
    ) {
        FraudSignal fraudSignal = fraudSignalService.getById(signalId)
                .orElseThrow(() -> new FraudSignalNotFoundException(signalId));

        User citizen = userRepository.findById(requestDto.citizenId())
                .orElseThrow(() -> new RuntimeException("Citizen not found with ID: " + requestDto.citizenId()));

        WasteReport report = wasteReportService.getById(requestDto.reportId())
                .orElseThrow(() -> new WasteReportNotFoundException(requestDto.reportId()));

        fraudSignal.setCitizen(citizen);
        fraudSignal.setReport(report);
        fraudSignal.setType(requestDto.type());
        fraudSignal.setScore(requestDto.score());

        FraudSignal updatedSignal = fraudSignalService.update(signalId, fraudSignal);
        return ResponseEntity.ok(toResponseDto(updatedSignal));
    }

    @DeleteMapping("/{signalId}")
    @Transactional
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN','ENTERPRISE_ADMIN')")
    @Operation(
            summary = "Delete fraud signal",
            description = "Deletes a fraud signal by ID",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Fraud signal deleted"),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(
                    schema = @Schema(implementation = ErrorResponseDto.class)
            )),
            @ApiResponse(responseCode = "404", description = "Fraud signal not found", content = @Content(
                    schema = @Schema(implementation = ErrorResponseDto.class)
            ))
    })
    public ResponseEntity<Void> deleteFraudSignal(
            @Parameter(description = "Fraud Signal ID", required = true, example = "a0b1c2d3-e4f5-6789-abcd-ef0123456789")
            @PathVariable UUID signalId
    ) {
        fraudSignalService.delete(signalId);
        return ResponseEntity.noContent().build();
    }

    private FraudSignalResponseDto toResponseDto(FraudSignal fraudSignal) {
        return new FraudSignalResponseDto(
                fraudSignal.getId(),
                fraudSignal.getCitizen().getId(),
                fraudSignal.getCitizen().getName(),
                fraudSignal.getReport().getId(),
                fraudSignal.getType(),
                fraudSignal.getScore(),
                fraudSignal.getCreatedAt()
        );
    }
}


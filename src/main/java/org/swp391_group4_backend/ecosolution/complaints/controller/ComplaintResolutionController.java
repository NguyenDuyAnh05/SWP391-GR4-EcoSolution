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
import org.springframework.web.bind.annotation.RestController;
import org.swp391_group4_backend.ecosolution.auth.domain.entity.User;
import org.swp391_group4_backend.ecosolution.auth.domain.entity.UserAuth;
import org.swp391_group4_backend.ecosolution.auth.repository.UserAuthRepository;
import org.swp391_group4_backend.ecosolution.common.domain.dto.response.ErrorResponseDto;
import org.swp391_group4_backend.ecosolution.complaints.domain.dto.request.ComplaintResolutionRequestDto;
import org.swp391_group4_backend.ecosolution.complaints.domain.dto.response.ComplaintResolutionResponseDto;
import org.swp391_group4_backend.ecosolution.complaints.domain.entity.Complaint;
import org.swp391_group4_backend.ecosolution.complaints.domain.entity.ComplaintResolution;
import org.swp391_group4_backend.ecosolution.complaints.exception.ComplaintNotFoundException;
import org.swp391_group4_backend.ecosolution.complaints.exception.ComplaintResolutionNotFoundException;
import org.swp391_group4_backend.ecosolution.complaints.service.ComplaintResolutionService;
import org.swp391_group4_backend.ecosolution.complaints.service.ComplaintService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/complaint-resolutions")
@Tag(name = "Complaint Resolutions", description = "Complaint resolution management")
public class ComplaintResolutionController {
    private final ComplaintResolutionService complaintResolutionService;
    private final ComplaintService complaintService;
    private final UserAuthRepository userAuthRepository;

    public ComplaintResolutionController(ComplaintResolutionService complaintResolutionService,
                                          ComplaintService complaintService,
                                          UserAuthRepository userAuthRepository) {
        this.complaintResolutionService = complaintResolutionService;
        this.complaintService = complaintService;
        this.userAuthRepository = userAuthRepository;
    }

    @PostMapping
    @Transactional
    @PreAuthorize("hasAnyRole('ENTERPRISE_ADMIN','SYSTEM_ADMIN')")
    @Operation(
            summary = "Create a complaint resolution",
            description = "Creates a resolution for a complaint",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Resolution created", content = @Content(
                    schema = @Schema(implementation = ComplaintResolutionResponseDto.class)
            )),
            @ApiResponse(responseCode = "400", description = "Invalid request", content = @Content(
                    schema = @Schema(implementation = ErrorResponseDto.class)
            )),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(
                    schema = @Schema(implementation = ErrorResponseDto.class)
            )),
            @ApiResponse(responseCode = "404", description = "Complaint not found", content = @Content(
                    schema = @Schema(implementation = ErrorResponseDto.class)
            ))
    })
    public ResponseEntity<ComplaintResolutionResponseDto> createResolution(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Resolution creation payload", required = true)
            @RequestBody @Valid ComplaintResolutionRequestDto requestDto,
            Authentication authentication
    ) {
        String username = authentication.getName();
        UserAuth userAuth = userAuthRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        User admin = userAuth.getUser();

        Complaint complaint = complaintService.getById(requestDto.complaintId())
                .orElseThrow(() -> new ComplaintNotFoundException(requestDto.complaintId()));

        ComplaintResolution resolution = ComplaintResolution.builder()
                .complaint(complaint)
                .admin(admin)
                .result(requestDto.result())
                .note(requestDto.note())
                .createdAt(LocalDateTime.now())
                .build();

        ComplaintResolution createdResolution = complaintResolutionService.create(resolution);
        return new ResponseEntity<>(toResponseDto(createdResolution), HttpStatus.CREATED);
    }

    @GetMapping("/{resolutionId}")
    @Transactional(readOnly = true)
    @PreAuthorize("hasAnyRole('CITIZEN','ENTERPRISE_ADMIN','SYSTEM_ADMIN')")
    @Operation(
            summary = "Get resolution by ID",
            description = "Returns a complaint resolution detail by resolution identifier",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Resolution found", content = @Content(
                    schema = @Schema(implementation = ComplaintResolutionResponseDto.class)
            )),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(
                    schema = @Schema(implementation = ErrorResponseDto.class)
            )),
            @ApiResponse(responseCode = "404", description = "Resolution not found", content = @Content(
                    schema = @Schema(implementation = ErrorResponseDto.class)
            ))
    })
    public ResponseEntity<ComplaintResolutionResponseDto> getResolutionById(
            @Parameter(description = "Resolution ID", required = true, example = "a0b1c2d3-e4f5-6789-abcd-ef0123456789")
            @PathVariable UUID resolutionId
    ) {
        ComplaintResolution resolution = complaintResolutionService.getById(resolutionId)
                .orElseThrow(() -> new ComplaintResolutionNotFoundException(resolutionId));
        return ResponseEntity.ok(toResponseDto(resolution));
    }

    @GetMapping
    @Transactional(readOnly = true)
    @PreAuthorize("hasAnyRole('CITIZEN','ENTERPRISE_ADMIN','SYSTEM_ADMIN')")
    @Operation(
            summary = "Get all resolutions",
            description = "Returns all complaint resolutions",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Resolutions retrieved", content = @Content(
                    schema = @Schema(implementation = ComplaintResolutionResponseDto.class)
            )),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(
                    schema = @Schema(implementation = ErrorResponseDto.class)
            ))
    })
    public ResponseEntity<List<ComplaintResolutionResponseDto>> getAllResolutions() {
        List<ComplaintResolutionResponseDto> response = complaintResolutionService.getAll()
                .stream()
                .map(this::toResponseDto)
                .toList();
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{resolutionId}")
    @Transactional
    @PreAuthorize("hasAnyRole('ENTERPRISE_ADMIN','SYSTEM_ADMIN')")
    @Operation(
            summary = "Update resolution",
            description = "Updates a complaint resolution",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Resolution updated", content = @Content(
                    schema = @Schema(implementation = ComplaintResolutionResponseDto.class)
            )),
            @ApiResponse(responseCode = "400", description = "Invalid request", content = @Content(
                    schema = @Schema(implementation = ErrorResponseDto.class)
            )),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(
                    schema = @Schema(implementation = ErrorResponseDto.class)
            )),
            @ApiResponse(responseCode = "404", description = "Resolution not found", content = @Content(
                    schema = @Schema(implementation = ErrorResponseDto.class)
            ))
    })
    public ResponseEntity<ComplaintResolutionResponseDto> updateResolution(
            @Parameter(description = "Resolution ID", required = true, example = "a0b1c2d3-e4f5-6789-abcd-ef0123456789")
            @PathVariable UUID resolutionId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Resolution update payload", required = true)
            @RequestBody @Valid ComplaintResolutionRequestDto requestDto
    ) {
        ComplaintResolution resolution = complaintResolutionService.getById(resolutionId)
                .orElseThrow(() -> new ComplaintResolutionNotFoundException(resolutionId));

        Complaint complaint = complaintService.getById(requestDto.complaintId())
                .orElseThrow(() -> new ComplaintNotFoundException(requestDto.complaintId()));

        resolution.setComplaint(complaint);
        resolution.setResult(requestDto.result());
        resolution.setNote(requestDto.note());

        ComplaintResolution updatedResolution = complaintResolutionService.update(resolutionId, resolution);
        return ResponseEntity.ok(toResponseDto(updatedResolution));
    }

    @DeleteMapping("/{resolutionId}")
    @Transactional
    @PreAuthorize("hasAnyRole('ENTERPRISE_ADMIN','SYSTEM_ADMIN')")
    @Operation(
            summary = "Delete resolution",
            description = "Deletes a complaint resolution by ID",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Resolution deleted"),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(
                    schema = @Schema(implementation = ErrorResponseDto.class)
            )),
            @ApiResponse(responseCode = "404", description = "Resolution not found", content = @Content(
                    schema = @Schema(implementation = ErrorResponseDto.class)
            ))
    })
    public ResponseEntity<Void> deleteResolution(
            @Parameter(description = "Resolution ID", required = true, example = "a0b1c2d3-e4f5-6789-abcd-ef0123456789")
            @PathVariable UUID resolutionId
    ) {
        complaintResolutionService.delete(resolutionId);
        return ResponseEntity.noContent().build();
    }

    private ComplaintResolutionResponseDto toResponseDto(ComplaintResolution resolution) {
        return new ComplaintResolutionResponseDto(
                resolution.getId(),
                resolution.getComplaint().getId(),
                resolution.getAdmin().getId(),
                resolution.getAdmin().getName(),
                resolution.getResult(),
                resolution.getNote(),
                resolution.getCreatedAt()
        );
    }
}




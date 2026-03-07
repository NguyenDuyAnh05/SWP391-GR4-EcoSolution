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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.swp391_group4_backend.ecosolution.common.domain.dto.response.ErrorResponseDto;
import org.swp391_group4_backend.ecosolution.reports.domain.dto.request.WasteReportImageCreateRequestDto;
import org.swp391_group4_backend.ecosolution.reports.domain.dto.response.WasteReportImageResponseDto;
import org.swp391_group4_backend.ecosolution.reports.domain.entity.WasteReport;
import org.swp391_group4_backend.ecosolution.reports.domain.entity.WasteReportImage;
import org.swp391_group4_backend.ecosolution.reports.exception.WasteReportImageNotFoundException;
import org.swp391_group4_backend.ecosolution.reports.exception.WasteReportNotFoundException;
import org.swp391_group4_backend.ecosolution.reports.repository.WasteReportRepository;
import org.swp391_group4_backend.ecosolution.reports.service.WasteReportImageService;

@RestController
@RequestMapping("/api/v1/report-images")
@Tag(name = "Waste Report Images", description = "Image evidence management for waste reports")
public class WasteReportImageController {

  private final WasteReportImageService wasteReportImageService;
  private final WasteReportRepository wasteReportRepository;

  public WasteReportImageController(
      WasteReportImageService wasteReportImageService,
      WasteReportRepository wasteReportRepository
  ) {
    this.wasteReportImageService = wasteReportImageService;
    this.wasteReportRepository = wasteReportRepository;
  }

  @PostMapping
  @Transactional
  @PreAuthorize("hasAnyRole('CITIZEN','COLLECTOR','ASSIGNOR','ENTERPRISE_ADMIN','SYSTEM_ADMIN')")
  @Operation(
      summary = "Upload report image",
      description = "Add an image to a waste report",
      security = @SecurityRequirement(name = "bearerAuth")
  )
  @ApiResponses({
      @ApiResponse(responseCode = "201", description = "Image uploaded", content = @Content(
          schema = @Schema(implementation = WasteReportImageResponseDto.class)
      )),
      @ApiResponse(responseCode = "400", description = "Invalid request", content = @Content(
          schema = @Schema(implementation = ErrorResponseDto.class)
      )),
      @ApiResponse(responseCode = "404", description = "Report not found", content = @Content(
          schema = @Schema(implementation = ErrorResponseDto.class)
      ))
  })
  public ResponseEntity<WasteReportImageResponseDto> uploadImage(
      @RequestBody @Valid WasteReportImageCreateRequestDto requestDto
  ) {
    WasteReport report = wasteReportRepository.findById(requestDto.reportId())
        .orElseThrow(() -> new WasteReportNotFoundException(requestDto.reportId()));

    WasteReportImage image = WasteReportImage.builder()
        .report(report)
        .imageUrl(requestDto.imageUrl())
        .createdAt(LocalDateTime.now())
        .build();

    WasteReportImage savedImage = wasteReportImageService.create(image);
    return new ResponseEntity<>(toResponse(savedImage), HttpStatus.CREATED);
  }

  @GetMapping("/{imageId}")
  @Transactional(readOnly = true)
  @PreAuthorize("hasAnyRole('CITIZEN','COLLECTOR','ASSIGNOR','ENTERPRISE_ADMIN','SYSTEM_ADMIN')")
  @Operation(
      summary = "Get image by id",
      description = "Retrieve a report image by identifier",
      security = @SecurityRequirement(name = "bearerAuth")
  )
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Image found", content = @Content(
          schema = @Schema(implementation = WasteReportImageResponseDto.class)
      )),
      @ApiResponse(responseCode = "404", description = "Image not found", content = @Content(
          schema = @Schema(implementation = ErrorResponseDto.class)
      ))
  })
  public ResponseEntity<WasteReportImageResponseDto> getImageById(
      @PathVariable UUID imageId
  ) {
    WasteReportImage image = wasteReportImageService.getById(imageId)
        .orElseThrow(() -> new WasteReportImageNotFoundException(imageId));
    return ResponseEntity.ok(toResponse(image));
  }

  @GetMapping
  @Transactional(readOnly = true)
  @PreAuthorize("hasAnyRole('CITIZEN','COLLECTOR','ASSIGNOR','ENTERPRISE_ADMIN','SYSTEM_ADMIN')")
  @Operation(
      summary = "Get report images",
      description = "Get all images, optionally filtered by report",
      security = @SecurityRequirement(name = "bearerAuth")
  )
  public ResponseEntity<List<WasteReportImageResponseDto>> getImages(
      @Parameter(description = "Filter by report ID")
      @RequestParam(required = false) UUID reportId
  ) {
    List<WasteReportImage> images = wasteReportImageService.getAll();

    if (reportId != null) {
      images = images.stream()
          .filter(img -> img.getReport().getId().equals(reportId))
          .collect(Collectors.toList());
    }

    List<WasteReportImageResponseDto> responseDtos = images.stream()
        .map(this::toResponse)
        .collect(Collectors.toList());

    return ResponseEntity.ok(responseDtos);
  }

  @DeleteMapping("/{imageId}")
  @Transactional
  @PreAuthorize("hasAnyRole('CITIZEN','COLLECTOR','ASSIGNOR','ENTERPRISE_ADMIN','SYSTEM_ADMIN')")
  @Operation(
      summary = "Delete report image",
      description = "Remove an image from a report",
      security = @SecurityRequirement(name = "bearerAuth")
  )
  @ApiResponses({
      @ApiResponse(responseCode = "204", description = "Image deleted"),
      @ApiResponse(responseCode = "404", description = "Image not found", content = @Content(
          schema = @Schema(implementation = ErrorResponseDto.class)
      ))
  })
  public ResponseEntity<Void> deleteImage(
      @PathVariable UUID imageId
  ) {
    if (wasteReportImageService.getById(imageId).isEmpty()) {
      throw new WasteReportImageNotFoundException(imageId);
    }
    wasteReportImageService.delete(imageId);
    return ResponseEntity.noContent().build();
  }

  private WasteReportImageResponseDto toResponse(WasteReportImage image) {
    return new WasteReportImageResponseDto(
        image.getId(),
        image.getReport().getId(),
        image.getImageUrl(),
        image.getCreatedAt()
    );
  }
}


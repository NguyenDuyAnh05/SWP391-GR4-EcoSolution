package org.swp391_group4_backend.ecosolution.collectors.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.swp391_group4_backend.ecosolution.auth.domain.entity.User;
import org.swp391_group4_backend.ecosolution.auth.domain.entity.UserRole;
import org.swp391_group4_backend.ecosolution.auth.repository.UserRepository;
import org.swp391_group4_backend.ecosolution.collectors.domain.dto.request.CollectorScoreUpsertRequestDto;
import org.swp391_group4_backend.ecosolution.collectors.domain.dto.request.CollectorStatusHistoryCreateRequestDto;
import org.swp391_group4_backend.ecosolution.collectors.domain.dto.response.CollectorScoreResponseDto;
import org.swp391_group4_backend.ecosolution.collectors.domain.dto.response.CollectorStatusHistoryResponseDto;
import org.swp391_group4_backend.ecosolution.collectors.domain.entity.CollectorScore;
import org.swp391_group4_backend.ecosolution.collectors.domain.entity.CollectorStatusHistory;
import org.swp391_group4_backend.ecosolution.collectors.exception.CollectorNotFoundException;
import org.swp391_group4_backend.ecosolution.collectors.service.CollectorScoreService;
import org.swp391_group4_backend.ecosolution.collectors.service.CollectorStatusHistoryService;
import org.swp391_group4_backend.ecosolution.common.domain.dto.response.ErrorResponseDto;
import org.swp391_group4_backend.ecosolution.tasks.domain.entity.TaskStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/collectors")
@Validated
@Tag(name = "Collectors", description = "Collector score and status history management")
public class CollectorController {

  private final CollectorScoreService collectorScoreService;
  private final CollectorStatusHistoryService collectorStatusHistoryService;
  private final UserRepository userRepository;

  public CollectorController(
      CollectorScoreService collectorScoreService,
      CollectorStatusHistoryService collectorStatusHistoryService,
      UserRepository userRepository
  ) {
    this.collectorScoreService = collectorScoreService;
    this.collectorStatusHistoryService = collectorStatusHistoryService;
    this.userRepository = userRepository;
  }

  @GetMapping("/scores")
  @Transactional(readOnly = true)
  @PreAuthorize("hasAnyRole('ENTERPRISE_ADMIN','SYSTEM_ADMIN','ASSIGNOR','COLLECTOR')")
  @Operation(
      summary = "Get collector scores",
      description = "Returns collector scores with optional filters and pagination",
      security = @SecurityRequirement(name = "bearerAuth")
  )
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Scores retrieved"),
      @ApiResponse(responseCode = "403", description = "Access denied", content = @Content(
          schema = @Schema(implementation = ErrorResponseDto.class)
      ))
  })
  public ResponseEntity<List<CollectorScoreResponseDto>> getAllScores(
      @Parameter(description = "Minimum reliability score")
      @RequestParam(required = false) BigDecimal minReliability,
      @Parameter(description = "Maximum complaint rate")
      @RequestParam(required = false) BigDecimal maxComplaintRate,
      @Parameter(description = "Zero-based page index")
      @RequestParam(defaultValue = "0") @Min(0) int page,
      @Parameter(description = "Page size (1-100)")
      @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size
  ) {
    List<CollectorScoreResponseDto> response = collectorScoreService.getAll()
        .stream()
        .map(this::toScoreResponse)
        .filter(dto -> minReliability == null || dto.reliabilityScore() != null
            && dto.reliabilityScore().compareTo(minReliability) >= 0)
        .filter(dto -> maxComplaintRate == null || dto.complaintRate() != null
            && dto.complaintRate().compareTo(maxComplaintRate) <= 0)
        .sorted(Comparator.comparing(CollectorScoreResponseDto::reliabilityScore,
                Comparator.nullsLast(Comparator.reverseOrder()))
            .thenComparing(CollectorScoreResponseDto::updatedAt, Comparator.nullsLast(Comparator.reverseOrder())))
        .toList();

    return ResponseEntity.ok(paginate(response, page, size));
  }

  @GetMapping("/{collectorId}/score")
  @Transactional(readOnly = true)
  @PreAuthorize("hasAnyRole('ENTERPRISE_ADMIN','SYSTEM_ADMIN','ASSIGNOR','COLLECTOR')")
  @Operation(
      summary = "Get collector score by collector id",
      security = @SecurityRequirement(name = "bearerAuth")
  )
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Score retrieved"),
      @ApiResponse(responseCode = "404", description = "Collector not found", content = @Content(
          schema = @Schema(implementation = ErrorResponseDto.class)
      ))
  })
  public ResponseEntity<CollectorScoreResponseDto> getCollectorScore(@PathVariable UUID collectorId) {
    CollectorScore collectorScore = collectorScoreService.getByCollectorId(collectorId)
        .orElseThrow(() -> new CollectorNotFoundException(collectorId));

    return ResponseEntity.ok(toScoreResponse(collectorScore));
  }

  @PutMapping("/{collectorId}/score")
  @Transactional
  @PreAuthorize("hasAnyRole('ENTERPRISE_ADMIN','SYSTEM_ADMIN','ASSIGNOR')")
  @Operation(
      summary = "Create or update collector score",
      security = @SecurityRequirement(name = "bearerAuth")
  )
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Score upserted"),
      @ApiResponse(responseCode = "400", description = "Validation failed", content = @Content(
          schema = @Schema(implementation = ErrorResponseDto.class)
      )),
      @ApiResponse(responseCode = "404", description = "Collector not found", content = @Content(
          schema = @Schema(implementation = ErrorResponseDto.class)
      ))
  })
  public ResponseEntity<CollectorScoreResponseDto> upsertCollectorScore(
      @PathVariable UUID collectorId,
      @RequestBody @Valid CollectorScoreUpsertRequestDto requestDto
  ) {
    User collector = getCollectorOrThrow(collectorId);

    CollectorScore collectorScore = collectorScoreService.getByCollectorId(collectorId)
        .orElseGet(() -> CollectorScore.builder().collectorId(collectorId).build());

    collectorScore.setCollector(collector);
    collectorScore.setCollectorId(collectorId);
    collectorScore.setResponseRate(requestDto.responseRate());
    collectorScore.setCompletionRate(requestDto.completionRate());
    collectorScore.setComplaintRate(requestDto.complaintRate());
    collectorScore.setReliabilityScore(requestDto.reliabilityScore());
    collectorScore.setUpdatedAt(LocalDateTime.now());

    CollectorScore savedCollectorScore = collectorScoreService.create(collectorScore);
    return ResponseEntity.ok(toScoreResponse(savedCollectorScore));
  }

  @GetMapping("/{collectorId}/status-history")
  @Transactional(readOnly = true)
  @PreAuthorize("hasAnyRole('ENTERPRISE_ADMIN','SYSTEM_ADMIN','ASSIGNOR','COLLECTOR')")
  @Operation(
      summary = "Get collector status history",
      description = "Returns status history with optional transition/date filters and pagination",
      security = @SecurityRequirement(name = "bearerAuth")
  )
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Status history retrieved"),
      @ApiResponse(responseCode = "404", description = "Collector not found", content = @Content(
          schema = @Schema(implementation = ErrorResponseDto.class)
      ))
  })
  public ResponseEntity<List<CollectorStatusHistoryResponseDto>> getCollectorStatusHistory(
      @PathVariable UUID collectorId,
      @Parameter(description = "Filter by from status")
      @RequestParam(required = false) TaskStatus fromStatus,
      @Parameter(description = "Filter by to status")
      @RequestParam(required = false) TaskStatus toStatus,
      @Parameter(description = "Include changes at or after this datetime")
      @RequestParam(required = false)
      @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime changedAfter,
      @Parameter(description = "Include changes at or before this datetime")
      @RequestParam(required = false)
      @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime changedBefore,
      @Parameter(description = "Zero-based page index")
      @RequestParam(defaultValue = "0") @Min(0) int page,
      @Parameter(description = "Page size (1-100)")
      @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size
  ) {
    getCollectorOrThrow(collectorId);

    List<CollectorStatusHistoryResponseDto> response = collectorStatusHistoryService.getByCollectorId(collectorId)
        .stream()
        .map(this::toStatusHistoryResponse)
        .filter(dto -> fromStatus == null || dto.statusFrom() == fromStatus)
        .filter(dto -> toStatus == null || dto.statusTo() == toStatus)
        .filter(dto -> changedAfter == null || dto.changedAt() != null && !dto.changedAt().isBefore(changedAfter))
        .filter(dto -> changedBefore == null || dto.changedAt() != null && !dto.changedAt().isAfter(changedBefore))
        .sorted(Comparator.comparing(CollectorStatusHistoryResponseDto::changedAt,
            Comparator.nullsLast(Comparator.reverseOrder())))
        .toList();

    return ResponseEntity.ok(paginate(response, page, size));
  }

  @PostMapping("/{collectorId}/status-history")
  @Transactional
  @PreAuthorize("hasAnyRole('ENTERPRISE_ADMIN','SYSTEM_ADMIN','ASSIGNOR')")
  @Operation(
      summary = "Create collector status history record",
      security = @SecurityRequirement(name = "bearerAuth")
  )
  @ApiResponses({
      @ApiResponse(responseCode = "201", description = "Status history created"),
      @ApiResponse(responseCode = "400", description = "Validation failed", content = @Content(
          schema = @Schema(implementation = ErrorResponseDto.class)
      )),
      @ApiResponse(responseCode = "404", description = "Collector not found", content = @Content(
          schema = @Schema(implementation = ErrorResponseDto.class)
      ))
  })
  public ResponseEntity<CollectorStatusHistoryResponseDto> createCollectorStatusHistory(
      @PathVariable UUID collectorId,
      @RequestBody @Valid CollectorStatusHistoryCreateRequestDto requestDto
  ) {
    User collector = getCollectorOrThrow(collectorId);

    CollectorStatusHistory collectorStatusHistory = CollectorStatusHistory.builder()
        .collector(collector)
        .statusFrom(requestDto.statusFrom())
        .statusTo(requestDto.statusTo())
        .reason(requestDto.reason())
        .changedAt(LocalDateTime.now())
        .build();

    CollectorStatusHistory savedCollectorStatusHistory = collectorStatusHistoryService.create(collectorStatusHistory);

    return new ResponseEntity<>(toStatusHistoryResponse(savedCollectorStatusHistory), HttpStatus.CREATED);
  }

  private User getCollectorOrThrow(UUID collectorId) {
    User user = userRepository.findById(collectorId)
        .orElseThrow(() -> new CollectorNotFoundException(collectorId));

    if (user.getRole() != UserRole.COLLECTOR) {
      throw new CollectorNotFoundException(collectorId);
    }

    return user;
  }

  private CollectorScoreResponseDto toScoreResponse(CollectorScore collectorScore) {
    String collectorName = collectorScore.getCollector() != null ? collectorScore.getCollector().getName() : null;

    return new CollectorScoreResponseDto(
        collectorScore.getCollectorId(),
        collectorName,
        collectorScore.getResponseRate(),
        collectorScore.getCompletionRate(),
        collectorScore.getComplaintRate(),
        collectorScore.getReliabilityScore(),
        collectorScore.getUpdatedAt()
    );
  }

  private CollectorStatusHistoryResponseDto toStatusHistoryResponse(CollectorStatusHistory collectorStatusHistory) {
    return new CollectorStatusHistoryResponseDto(
        collectorStatusHistory.getId(),
        collectorStatusHistory.getCollector().getId(),
        collectorStatusHistory.getStatusFrom(),
        collectorStatusHistory.getStatusTo(),
        collectorStatusHistory.getReason(),
        collectorStatusHistory.getChangedAt()
    );
  }

  private <T> List<T> paginate(List<T> source, int page, int size) {
    int fromIndex = Math.min(page * size, source.size());
    int toIndex = Math.min(fromIndex + size, source.size());
    return source.subList(fromIndex, toIndex);
  }
}


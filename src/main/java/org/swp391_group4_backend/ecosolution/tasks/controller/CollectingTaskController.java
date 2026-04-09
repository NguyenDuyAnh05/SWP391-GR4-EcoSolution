package org.swp391_group4_backend.ecosolution.tasks.controller;
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
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.swp391_group4_backend.ecosolution.common.domain.dto.response.ErrorResponseDto;
import org.swp391_group4_backend.ecosolution.tasks.domain.dto.request.CollectingTaskAssignRequestDto;
import org.swp391_group4_backend.ecosolution.tasks.domain.dto.request.CollectingTaskStatusUpdateRequestDto;
import org.swp391_group4_backend.ecosolution.tasks.domain.dto.response.CollectingTaskResponseDto;
import org.swp391_group4_backend.ecosolution.tasks.domain.entity.CollectingTask;
import org.swp391_group4_backend.ecosolution.tasks.domain.entity.TaskStatus;
import org.swp391_group4_backend.ecosolution.tasks.exception.TaskNotFoundException;
import org.swp391_group4_backend.ecosolution.tasks.service.CollectingTaskService;
import java.util.List;
import java.util.UUID;
@RestController
@RequestMapping("/api/v1/tasks")
@Tag(name = "Tasks", description = "Task assignment and task lifecycle management")
public class CollectingTaskController {
  private final CollectingTaskService collectingTaskService;
  public CollectingTaskController(CollectingTaskService collectingTaskService) {
    this.collectingTaskService = collectingTaskService;
  }
  @PostMapping
  @Transactional
  @PreAuthorize("hasAnyRole('ENTERPRISE_ADMIN','SYSTEM_ADMIN','ASSIGNOR')")
  @Operation(
      summary = "Assign a task",
      description = "Creates a collecting task for a report and assigns it to a collector",
      security = @SecurityRequirement(name = "bearerAuth")
  )
  @ApiResponses({
      @ApiResponse(responseCode = "201", description = "Task created", content = @Content(
          schema = @Schema(implementation = CollectingTaskResponseDto.class)
      )),
      @ApiResponse(responseCode = "400", description = "Invalid request", content = @Content(
          schema = @Schema(implementation = ErrorResponseDto.class)
      )),
      @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(
          schema = @Schema(implementation = ErrorResponseDto.class)
      )),
      @ApiResponse(responseCode = "403", description = "Access denied", content = @Content(
          schema = @Schema(implementation = ErrorResponseDto.class)
      ))
  })
  public ResponseEntity<CollectingTaskResponseDto> assignTask(
      @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Task assignment payload", required = true)
      @org.springframework.web.bind.annotation.RequestBody @Valid CollectingTaskAssignRequestDto requestDto
  ) {
    CollectingTask collectingTask = collectingTaskService.assignTask(requestDto);
    return new ResponseEntity<>(toResponseDto(collectingTask), HttpStatus.CREATED);
  }
  @GetMapping("/{taskId}")
  @Transactional(readOnly = true)
  @PreAuthorize("hasAnyRole('ENTERPRISE_ADMIN','SYSTEM_ADMIN','ASSIGNOR','COLLECTOR')")
  @Operation(
      summary = "Get task by id",
      description = "Returns a task detail by task identifier",
      security = @SecurityRequirement(name = "bearerAuth")
  )
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Task found", content = @Content(
          schema = @Schema(implementation = CollectingTaskResponseDto.class)
      )),
      @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(
          schema = @Schema(implementation = ErrorResponseDto.class)
      )),
      @ApiResponse(responseCode = "404", description = "Task not found", content = @Content(
          schema = @Schema(implementation = ErrorResponseDto.class)
      ))
  })
  public ResponseEntity<CollectingTaskResponseDto> getTaskById(
      @Parameter(description = "Task ID", required = true, example = "a0b1c2d3-e4f5-6789-abcd-ef0123456789")
      @PathVariable UUID taskId
  ) {
    CollectingTask collectingTask = collectingTaskService.getById(taskId)
        .orElseThrow(() -> new TaskNotFoundException(taskId));
    return ResponseEntity.ok(toResponseDto(collectingTask));
  }
  @GetMapping
  @Transactional(readOnly = true)
  @PreAuthorize("hasAnyRole('ENTERPRISE_ADMIN','SYSTEM_ADMIN','ASSIGNOR','COLLECTOR')")
  @Operation(
      summary = "Get tasks",
      description = "Returns tasks with optional collector and status filters",
      security = @SecurityRequirement(name = "bearerAuth")
  )
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Tasks retrieved", content = @Content(
          schema = @Schema(implementation = CollectingTaskResponseDto.class)
      )),
      @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(
          schema = @Schema(implementation = ErrorResponseDto.class)
      ))
  })
  public ResponseEntity<List<CollectingTaskResponseDto>> getTasks(
      @Parameter(description = "Filter by collector id", example = "a0b1c2d3-e4f5-6789-abcd-ef0123456789")
      @RequestParam(required = false) UUID collectorId,
      @Parameter(description = "Filter by task status", example = "IN_PROGRESS")
      @RequestParam(required = false) TaskStatus status
  ) {
    List<CollectingTaskResponseDto> response = collectingTaskService.getAll(collectorId, status)
        .stream()
        .map(this::toResponseDto)
        .toList();
    return ResponseEntity.ok(response);
  }
  @PatchMapping("/{taskId}/status")
  @Transactional
  @PreAuthorize("hasAnyRole('ENTERPRISE_ADMIN','SYSTEM_ADMIN','ASSIGNOR','COLLECTOR')")
  @Operation(
      summary = "Update task status",
      description = "Updates task lifecycle status based on transition rules",
      security = @SecurityRequirement(name = "bearerAuth")
  )
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Task status updated", content = @Content(
          schema = @Schema(implementation = CollectingTaskResponseDto.class)
      )),
      @ApiResponse(responseCode = "400", description = "Invalid status transition", content = @Content(
          schema = @Schema(implementation = ErrorResponseDto.class)
      )),
      @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(
          schema = @Schema(implementation = ErrorResponseDto.class)
      )),
      @ApiResponse(responseCode = "404", description = "Task not found", content = @Content(
          schema = @Schema(implementation = ErrorResponseDto.class)
      ))
  })
  public ResponseEntity<CollectingTaskResponseDto> updateTaskStatus(
      @Parameter(description = "Task ID", required = true, example = "a0b1c2d3-e4f5-6789-abcd-ef0123456789")
      @PathVariable UUID taskId,
      @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "New status payload", required = true)
      @org.springframework.web.bind.annotation.RequestBody @Valid CollectingTaskStatusUpdateRequestDto requestDto
  ) {
    CollectingTask collectingTask = collectingTaskService.updateStatus(taskId, requestDto.status());
    return ResponseEntity.ok(toResponseDto(collectingTask));
  }
  @DeleteMapping("/{taskId}")
  @Transactional
  @PreAuthorize("hasAnyRole('ENTERPRISE_ADMIN','SYSTEM_ADMIN','ASSIGNOR')")
  @Operation(
      summary = "Delete task",
      description = "Deletes a task by id",
      security = @SecurityRequirement(name = "bearerAuth")
  )
  @ApiResponses({
      @ApiResponse(responseCode = "204", description = "Task deleted"),
      @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(
          schema = @Schema(implementation = ErrorResponseDto.class)
      )),
      @ApiResponse(responseCode = "404", description = "Task not found", content = @Content(
          schema = @Schema(implementation = ErrorResponseDto.class)
      ))
  })
  public ResponseEntity<Void> deleteTask(
      @Parameter(description = "Task ID", required = true, example = "a0b1c2d3-e4f5-6789-abcd-ef0123456789")
      @PathVariable UUID taskId
  ) {
    collectingTaskService.delete(taskId);
    return ResponseEntity.noContent().build();
  }
  private CollectingTaskResponseDto toResponseDto(CollectingTask collectingTask) {
    return new CollectingTaskResponseDto(
        collectingTask.getId(),
        collectingTask.getReport().getId(),
        collectingTask.getCollector().getId(),
        collectingTask.getCollector().getName(),
        collectingTask.getCurrentStatus(),
        collectingTask.getAssignedAt(),
        collectingTask.getStartedAt(),
        collectingTask.getCompletedAt()
    );
  }
}

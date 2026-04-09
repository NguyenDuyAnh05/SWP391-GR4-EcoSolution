package org.swp391_group4_backend.ecosolution.common.exception;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.swp391_group4_backend.ecosolution.auth.exception.EmailAlreadyExistsException;
import org.swp391_group4_backend.ecosolution.auth.exception.InvalidCredentialsException;
import org.swp391_group4_backend.ecosolution.auth.exception.InvalidRoleAssignmentException;
import org.swp391_group4_backend.ecosolution.auth.exception.UserNotFoundException;
import org.swp391_group4_backend.ecosolution.auth.exception.UsernameAlreadyExistsException;
import org.swp391_group4_backend.ecosolution.collectors.exception.CollectorNotFoundException;
import org.swp391_group4_backend.ecosolution.common.domain.dto.response.ErrorResponseDto;
import org.swp391_group4_backend.ecosolution.complaints.exception.ComplaintNotFoundException;
import org.swp391_group4_backend.ecosolution.complaints.exception.ComplaintResolutionNotFoundException;
import org.swp391_group4_backend.ecosolution.fraud.exception.FraudSignalNotFoundException;
import org.swp391_group4_backend.ecosolution.reports.exception.ReportStatusHistoryNotFoundException;
import org.swp391_group4_backend.ecosolution.reports.exception.WasteReportImageNotFoundException;
import org.swp391_group4_backend.ecosolution.reports.exception.WasteReportNotFoundException;
import org.swp391_group4_backend.ecosolution.tasks.exception.InvalidTaskTransitionException;
import org.swp391_group4_backend.ecosolution.tasks.exception.TaskAssignmentException;
import org.swp391_group4_backend.ecosolution.tasks.exception.TaskNotFoundException;
@ControllerAdvice
public class GlobalExceptionHandler {
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponseDto> handleValidationExceptions(MethodArgumentNotValidException ex) {
    String errorMessage = ex.getBindingResult()
        .getFieldErrors()
        .stream()
        .findFirst()
        .map(error -> error.getDefaultMessage())
        .orElse("Validation failed.");
    return new ResponseEntity<>(new ErrorResponseDto(errorMessage), HttpStatus.BAD_REQUEST);
  }
  @ExceptionHandler(EmailAlreadyExistsException.class)
  public ResponseEntity<ErrorResponseDto> handleExistedEmail(EmailAlreadyExistsException ex) {
    String email = ex.getEmail();
    String errorMessage = String.format("Email %s already exists", email);
    return new ResponseEntity<>(new ErrorResponseDto(errorMessage), HttpStatus.BAD_REQUEST);
  }
  @ExceptionHandler(UsernameAlreadyExistsException.class)
  public ResponseEntity<ErrorResponseDto> handleExistedUsername(UsernameAlreadyExistsException ex) {
    String errorMessage = String.format("Username %s already exists", ex.getUsername());
    return new ResponseEntity<>(new ErrorResponseDto(errorMessage), HttpStatus.BAD_REQUEST);
  }
  @ExceptionHandler(InvalidCredentialsException.class)
  public ResponseEntity<ErrorResponseDto> handleInvalidCredentials(InvalidCredentialsException ex) {
    return new ResponseEntity<>(new ErrorResponseDto(ex.getMessage()), HttpStatus.UNAUTHORIZED);
  }
  @ExceptionHandler(UserNotFoundException.class)
  public ResponseEntity<ErrorResponseDto> handleUserNotFound(UserNotFoundException ex) {
    String errorMessage = String.format("User %s not found", ex.getUserId());
    return new ResponseEntity<>(new ErrorResponseDto(errorMessage), HttpStatus.NOT_FOUND);
  }
  @ExceptionHandler(CollectorNotFoundException.class)
  public ResponseEntity<ErrorResponseDto> handleCollectorNotFound(CollectorNotFoundException ex) {
    String errorMessage = String.format("Collector %s not found", ex.getCollectorId());
    return new ResponseEntity<>(new ErrorResponseDto(errorMessage), HttpStatus.NOT_FOUND);
  }
  @ExceptionHandler(InvalidRoleAssignmentException.class)
  public ResponseEntity<ErrorResponseDto> handleInvalidRoleAssignment(InvalidRoleAssignmentException ex) {
    String errorMessage = String.format("Role %s cannot be assigned by this API", ex.getRole());
    return new ResponseEntity<>(new ErrorResponseDto(errorMessage), HttpStatus.BAD_REQUEST);
  }
  @ExceptionHandler(TaskNotFoundException.class)
  public ResponseEntity<ErrorResponseDto> handleTaskNotFound(TaskNotFoundException ex) {
    String errorMessage = String.format("Task %s not found", ex.getTaskId());
    return new ResponseEntity<>(new ErrorResponseDto(errorMessage), HttpStatus.NOT_FOUND);
  }
  @ExceptionHandler(TaskAssignmentException.class)
  public ResponseEntity<ErrorResponseDto> handleTaskAssignment(TaskAssignmentException ex) {
    return new ResponseEntity<>(new ErrorResponseDto(ex.getMessage()), HttpStatus.BAD_REQUEST);
  }
  @ExceptionHandler(WasteReportNotFoundException.class)
  public ResponseEntity<ErrorResponseDto> handleWasteReportNotFound(WasteReportNotFoundException ex) {
    String errorMessage = String.format("Waste report %s not found", ex.getReportId());
    return new ResponseEntity<>(new ErrorResponseDto(errorMessage), HttpStatus.NOT_FOUND);
  }
  @ExceptionHandler(WasteReportImageNotFoundException.class)
  public ResponseEntity<ErrorResponseDto> handleWasteReportImageNotFound(WasteReportImageNotFoundException ex) {
    String errorMessage = String.format("Waste report image %s not found", ex.getImageId());
    return new ResponseEntity<>(new ErrorResponseDto(errorMessage), HttpStatus.NOT_FOUND);
  }
  @ExceptionHandler(ReportStatusHistoryNotFoundException.class)
  public ResponseEntity<ErrorResponseDto> handleReportStatusHistoryNotFound(ReportStatusHistoryNotFoundException ex) {
    String errorMessage = String.format("Report status history %s not found", ex.getHistoryId());
    return new ResponseEntity<>(new ErrorResponseDto(errorMessage), HttpStatus.NOT_FOUND);
  }
  @ExceptionHandler(AccessDeniedException.class)
  public ResponseEntity<ErrorResponseDto> handleAccessDenied(AccessDeniedException ex) {
    return new ResponseEntity<>(new ErrorResponseDto("Access denied"), HttpStatus.FORBIDDEN);
  }
}

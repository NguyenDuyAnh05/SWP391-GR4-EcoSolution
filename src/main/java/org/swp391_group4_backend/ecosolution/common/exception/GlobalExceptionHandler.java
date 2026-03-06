package org.swp391_group4_backend.ecosolution.common.exception;

import org.swp391_group4_backend.ecosolution.auth.exception.EmailAlreadyExistsException;
import org.swp391_group4_backend.ecosolution.auth.exception.InvalidCredentialsException;
import org.swp391_group4_backend.ecosolution.auth.exception.InvalidRoleAssignmentException;
import org.swp391_group4_backend.ecosolution.auth.exception.UserNotFoundException;
import org.swp391_group4_backend.ecosolution.auth.exception.UsernameAlreadyExistsException;
import org.swp391_group4_backend.ecosolution.collectors.exception.CollectorNotFoundException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.swp391_group4_backend.ecosolution.common.domain.dto.response.ErrorResponseDto;

@ControllerAdvice
public class GlobalExceptionHandler {
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponseDto> handleValidationExceptions(MethodArgumentNotValidException ex) {

    String errorMessage = ex.getBindingResult()
            .getFieldErrors()
            .stream()
            .findFirst()
            .map(error -> error.getDefaultMessage())
            .orElse("Validation Failed.");


    ErrorResponseDto errorResponse = new ErrorResponseDto(errorMessage);

    return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(EmailAlreadyExistsException.class)
  public ResponseEntity<ErrorResponseDto> handleExistedEmail(EmailAlreadyExistsException ex) {
    String email = ex.getEmail();
    String errorMessage = String.format("Email %s already exists", email);
    ErrorResponseDto errorResponse = new ErrorResponseDto(errorMessage);

    return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
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

  @ExceptionHandler(AccessDeniedException.class)
  public ResponseEntity<ErrorResponseDto> handleAccessDenied(AccessDeniedException ex) {
    return new ResponseEntity<>(new ErrorResponseDto("Access denied"), HttpStatus.FORBIDDEN);
  }
}

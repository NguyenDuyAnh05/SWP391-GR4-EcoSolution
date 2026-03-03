package org.swp391_group4_backend.ecosolution.common.exception;
import org.swp391_group4_backend.ecosolution.auth.exception.EmailAlreadyExistsException;
import org.swp391_group4_backend.ecosolution.auth.exception.InvalidCredentialsException;
import org.swp391_group4_backend.ecosolution.auth.exception.InvalidRoleAssignmentException;
import org.swp391_group4_backend.ecosolution.auth.exception.UserNotFoundException;

import org.springframework.context.support.DefaultMessageSourceResolvable;
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

  @ExceptionHandler(UserNotFoundException.class)
  public ResponseEntity<ErrorResponseDto> handleUserNotFound(UserNotFoundException ex) {
    ErrorResponseDto errorResponse = new ErrorResponseDto(ex.getMessage());
    return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(InvalidRoleAssignmentException.class)
  public ResponseEntity<ErrorResponseDto> handleInvalidRoleAssignment(InvalidRoleAssignmentException ex) {
    ErrorResponseDto errorResponse = new ErrorResponseDto(ex.getMessage());
    return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(InvalidCredentialsException.class)
  public ResponseEntity<ErrorResponseDto> handleInvalidCredentials(InvalidCredentialsException ex) {
    ErrorResponseDto errorResponse = new ErrorResponseDto("Invalid username or password");
    return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
  }

  @ExceptionHandler(AccessDeniedException.class)
  public ResponseEntity<ErrorResponseDto> handleAccessDenied(AccessDeniedException ex) {
    ErrorResponseDto errorResponse = new ErrorResponseDto("Access denied. You don't have permission to perform this action.");
    return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
  }
}




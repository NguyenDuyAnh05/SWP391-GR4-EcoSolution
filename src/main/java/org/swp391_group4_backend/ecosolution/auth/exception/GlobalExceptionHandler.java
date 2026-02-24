package org.swp391_group4_backend.ecosolution.auth.exception;

import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.swp391_group4_backend.ecosolution.auth.domain.dto.response.ErrorResponseDto;

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
}

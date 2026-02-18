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

  @ExceptionHandler(UsernameAlreadyExistedException.class)
  public ResponseEntity<ErrorResponseDto> handleExistedUsername(UsernameAlreadyExistedException ex) {
    String username = ex.getUsername();
    String errorMessage = String.format("Username %s already existed", username);
    ErrorResponseDto errorResponse = new ErrorResponseDto(errorMessage);

    return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
  }
}

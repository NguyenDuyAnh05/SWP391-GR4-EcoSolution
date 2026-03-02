package org.swp391_group4_backend.ecosolution.common.exception;

import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.swp391_group4_backend.ecosolution.auth.exception.EmailAlreadyExistsException;
import org.swp391_group4_backend.ecosolution.common.domain.dto.response.ErrorResponseDto;
import org.swp391_group4_backend.ecosolution.wasterequest.exception.*;

@ControllerAdvice
public class GlobalExceptionHandler {

    // --- Validation errors (400) ---

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDto> handleValidationExceptions(MethodArgumentNotValidException ex) {
        String errorMessage = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .findFirst()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .orElse("Validation failed");
        return new ResponseEntity<>(new ErrorResponseDto(errorMessage), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(EvidenceRequiredException.class)
    public ResponseEntity<ErrorResponseDto> handleEvidenceRequired(EvidenceRequiredException ex) {
        return new ResponseEntity<>(new ErrorResponseDto(ex.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MissingRequestHeaderException.class)
    public ResponseEntity<ErrorResponseDto> handleMissingHeader(MissingRequestHeaderException ex) {
        return new ResponseEntity<>(new ErrorResponseDto("Missing required header: " + ex.getHeaderName()), HttpStatus.BAD_REQUEST);
    }

    // --- Auth errors (403) ---

    @ExceptionHandler(UnauthorizedRoleException.class)
    public ResponseEntity<ErrorResponseDto> handleUnauthorizedRole(UnauthorizedRoleException ex) {
        return new ResponseEntity<>(new ErrorResponseDto(ex.getMessage()), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(UserSuspendedException.class)
    public ResponseEntity<ErrorResponseDto> handleUserSuspended(UserSuspendedException ex) {
        return new ResponseEntity<>(new ErrorResponseDto(ex.getMessage()), HttpStatus.FORBIDDEN);
    }

    // --- Not found (404) ---

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleUserNotFound(UserNotFoundException ex) {
        return new ResponseEntity<>(new ErrorResponseDto(ex.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(WasteRequestNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleRequestNotFound(WasteRequestNotFoundException ex) {
        return new ResponseEntity<>(new ErrorResponseDto(ex.getMessage()), HttpStatus.NOT_FOUND);
    }

    // --- Conflict (409) ---

    @ExceptionHandler(InvalidStateTransitionException.class)
    public ResponseEntity<ErrorResponseDto> handleInvalidTransition(InvalidStateTransitionException ex) {
        return new ResponseEntity<>(new ErrorResponseDto(ex.getMessage()), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(DuplicateWasteRequestException.class)
    public ResponseEntity<ErrorResponseDto> handleDuplicateWasteRequest(DuplicateWasteRequestException ex) {
        return new ResponseEntity<>(new ErrorResponseDto(ex.getMessage()), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(DailyRequestLimitExceededException.class)
    public ResponseEntity<ErrorResponseDto> handleDailyLimit(DailyRequestLimitExceededException ex) {
        return new ResponseEntity<>(new ErrorResponseDto(ex.getMessage()), HttpStatus.CONFLICT);
    }

    // --- Too Many Requests (429) ---

    @ExceptionHandler(CancelCooldownException.class)
    public ResponseEntity<ErrorResponseDto> handleCancelCooldown(CancelCooldownException ex) {
        return new ResponseEntity<>(new ErrorResponseDto(ex.getMessage()), HttpStatus.TOO_MANY_REQUESTS);
    }

    // --- Legacy auth exceptions ---

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<ErrorResponseDto> handleExistedEmail(EmailAlreadyExistsException ex) {
        String errorMessage = String.format("Email %s already exists", ex.getEmail());
        return new ResponseEntity<>(new ErrorResponseDto(errorMessage), HttpStatus.BAD_REQUEST);
    }
}

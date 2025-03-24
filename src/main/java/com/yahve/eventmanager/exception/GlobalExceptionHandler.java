package com.yahve.eventmanager.exception;

import com.yahve.eventmanager.dto.ServerErrorDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {
  private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

  @ExceptionHandler(ResourceNotFoundException.class)
  public ResponseEntity<ServerErrorDto> handleResourceNotFound(ResourceNotFoundException e) {
    logger.error("Resource not found", e);
    var errorDto = new ServerErrorDto(
      "Entity not found",
      e.getMessage(),
      LocalDateTime.now()
    );
    return ResponseEntity
      .status(HttpStatus.NOT_FOUND)
      .body(errorDto);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
    Map<String, String> errors = new HashMap<>();
    ex.getBindingResult().getAllErrors().forEach(error -> {
      String fieldName = ((FieldError) error).getField();
      String errorMessage = error.getDefaultMessage();
      errors.put(fieldName, errorMessage);
    });
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ServerErrorDto> handleIllegalArgumentException(IllegalArgumentException e) {
    logger.warn("Validation error: {}", e.getMessage());
    var errorDto = new ServerErrorDto(
      "Validation error",
      e.getMessage(),
      LocalDateTime.now()
    );
    return ResponseEntity
      .status(HttpStatus.BAD_REQUEST)
      .body(errorDto);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ServerErrorDto> handleGenericException(Exception e) {
    logger.error("Server error", e);
    var errorDto = new ServerErrorDto(
      "Server error",
      e.getMessage(),
      LocalDateTime.now()
    );
    return ResponseEntity
      .status(HttpStatus.INTERNAL_SERVER_ERROR)
      .body(errorDto);
  }

  @ExceptionHandler(AuthorizationDeniedException.class)
  public ResponseEntity<ServerErrorDto> handleAuthorizationException(Exception e) {
    logger.error("Handle authorization exception", e);
    var errorDto = new ServerErrorDto(
      "Forbidden",
      e.getMessage(),
      LocalDateTime.now()
    );
    return ResponseEntity
      .status(HttpStatus.FORBIDDEN)
      .body(errorDto);
  }

  @ExceptionHandler(BusinessLogicException.class)
  public ResponseEntity<ServerErrorDto> handleBusinessLogicException(BusinessLogicException e) {
    logger.warn("Business logic error: {}", e.getMessage());
    var errorDto = new ServerErrorDto(
      "Business error",
      e.getMessage(),
      LocalDateTime.now()
    );
    return ResponseEntity.status(e.getStatus()).body(errorDto);
  }

  @ExceptionHandler(AuthenticationCredentialsNotFoundException.class)
  public ResponseEntity<ServerErrorDto> handleAuthenticationCredentialsNotFound(AuthenticationCredentialsNotFoundException e) {
    logger.warn("Authentication error: {}", e.getMessage());
    var errorDto = new ServerErrorDto(
      "Authentication error",
      e.getMessage(),
      LocalDateTime.now()
    );
    return ResponseEntity
      .status(HttpStatus.UNAUTHORIZED)
      .body(errorDto);
  }

  @ExceptionHandler(AccessDeniedException.class)
  public ResponseEntity<ServerErrorDto> handleAccessDenied(AccessDeniedException e) {
    logger.warn("Access denied: {}", e.getMessage());
    var errorDto = new ServerErrorDto(
      "Access denied",
      e.getMessage(),
      LocalDateTime.now()
    );
    return ResponseEntity
      .status(HttpStatus.FORBIDDEN)
      .body(errorDto);
  }


}

package com.yahve.eventmanager.exception;

import com.yahve.eventmanager.controller.LocationsController;
import com.yahve.eventmanager.dto.ServerErrorDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(LocationsController.class);

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
}

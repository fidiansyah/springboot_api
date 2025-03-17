package com.example.api_crud.exception;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import io.jsonwebtoken.JwtException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Handle validation errors from @Valid
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<LinkedHashMap<String, Object>> handleValidationErrors(
            MethodArgumentNotValidException ex) {

        LinkedHashMap<String, Object> response = new LinkedHashMap<>();
        Map<String, String> errors = new LinkedHashMap<>();

        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String message = error.getDefaultMessage();
            errors.put(fieldName, message);
        });

        response.put("status", "error");
        response.put("message", "Validation failed");
        response.put("errors", errors);

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    // Handle invalid UUID format
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<LinkedHashMap<String, Object>> handleIllegalArgument(
            IllegalArgumentException ex) {

        LinkedHashMap<String, Object> response = new LinkedHashMap<>();
        response.put("status", "error");
        response.put("message", "Invalid input format");
        response.put("details", ex.getMessage());

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    // Handle resource not found
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<LinkedHashMap<String, Object>> handleResourceNotFound(
            ResourceNotFoundException ex) {

        LinkedHashMap<String, Object> response = new LinkedHashMap<>();
        response.put("status", "error");
        response.put("message", ex.getMessage());

        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    // Handle invalid JSON format
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<LinkedHashMap<String, Object>> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex) {

        LinkedHashMap<String, Object> response = new LinkedHashMap<>();
        String message = "Invalid request format";

        if (ex.getCause() instanceof InvalidFormatException) {
            InvalidFormatException ife = (InvalidFormatException) ex.getCause();
            message = String.format("Invalid format for field '%s': %s",
                    ife.getPath().stream()
                            .map(JsonMappingException.Reference::getFieldName)
                            .collect(Collectors.joining(".")),
                    ife.getOriginalMessage());
        }

        response.put("status", "error");
        response.put("message", message);

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    // Handle file size limit exceeded
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<LinkedHashMap<String, Object>> handleMaxSizeException(
            MaxUploadSizeExceededException ex) {

        LinkedHashMap<String, Object> response = new LinkedHashMap<>();
        response.put("status", "error");
        response.put("message", "File size exceeds limit");
        response.put("details", "Maximum allowed size: 5MB");

        return new ResponseEntity<>(response, HttpStatus.PAYLOAD_TOO_LARGE);
    }

    // Handle security related exceptions
    @ExceptionHandler({ JwtException.class, AccessDeniedException.class })
    public ResponseEntity<LinkedHashMap<String, Object>> handleSecurityExceptions(
            Exception ex) {

        LinkedHashMap<String, Object> response = new LinkedHashMap<>();
        response.put("status", "error");
        response.put("message", "Authorization failed");
        response.put("details", ex.getMessage());

        HttpStatus status = ex instanceof AccessDeniedException ? HttpStatus.FORBIDDEN : HttpStatus.UNAUTHORIZED;

        return new ResponseEntity<>(response, status);
    }

    // Handle all other exceptions
    @ExceptionHandler(Exception.class)
    public ResponseEntity<LinkedHashMap<String, Object>> handleAllExceptions(
            Exception ex) {

        LinkedHashMap<String, Object> response = new LinkedHashMap<>();
        response.put("status", "error");
        response.put("message", "Internal server error");
        response.put("details", ex.getMessage());

        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
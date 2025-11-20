package com.fiap.nova.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class ValidaitonExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationErrors(MethodArgumentNotValidException ex) {
        List<String> errors = ex.getBindingResult().getFieldErrors()
                .stream()
                .map(error -> "Campo '" + error.getField() + "': " + error.getDefaultMessage())
                .collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("errors", errors);
        
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, Object>> handleDuplicateEntry(DataIntegrityViolationException ex) {
        String message = "Integrity data error";
        
        if (ex.getMessage() != null && ex.getMessage().contains("NOVA_USUARIO_EMAIL_UK")) {
            message = "This email already exists";
        }

        Map<String, Object> response = new HashMap<>();
        response.put("errors", List.of(message));

        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntimeException(RuntimeException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("errors", List.of(ex.getMessage() != null ? ex.getMessage() : "Intern error"));
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneralException(Exception ex) {
        ex.printStackTrace(); 
        Map<String, Object> response = new HashMap<>();
        response.put("errors", List.of("Unexpected error: " + ex.getMessage()));
        return ResponseEntity.internalServerError().body(response);
    }
}
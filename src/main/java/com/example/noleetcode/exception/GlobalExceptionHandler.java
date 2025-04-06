package com.example.noleetcode.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ApplicationException.class)
    public ResponseEntity<?> handleApplicationException(ApplicationException ex) {
        return ResponseEntity.status(ex.getStatus()).body(Map.of(
                "message", ex.getMessage(),
                "status", ex.getStatus().value(),
                "timestamp", Instant.now()
        ));
    }
}

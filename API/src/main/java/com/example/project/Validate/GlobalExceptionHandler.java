package com.example.project.Validate;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Bắt lỗi Validation
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, Object> response = new HashMap<>();

        // Lấy lỗi đầu tiên
        FieldError firstError = (FieldError) ex.getBindingResult().getAllErrors().get(0);
        String errorMessage = firstError.getDefaultMessage();

        response.put("status", HttpStatus.BAD_REQUEST.value());
        response.put("message", errorMessage);

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}

package com.kjava.common.exception;

import com.kjava.common.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse<Void>> handleRuntimeException(RuntimeException ex) {
        ApiResponse.ErrorResponse error = ApiResponse.ErrorResponse.builder()
                .code("BUSINESS_ERROR")
                .message(ex.getMessage())
                .build();
        
        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .success(false)
                .error(error)
                .build();
        
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGeneralException(Exception ex) {
        ApiResponse.ErrorResponse error = ApiResponse.ErrorResponse.builder()
                .code("INTERNAL_SERVER_ERROR")
                .message("Une erreur interne est survenue.")
                .build();
        
        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .success(false)
                .error(error)
                .build();
        
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

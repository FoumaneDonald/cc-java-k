package com.kjava.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApiError {
    private String code;
    private String message;
    private String details;

    public static ApiError of(String code, String message) {
        return ApiError.builder()
                .code(code)
                .message(message)
                .build();
    }

    public static ApiError of(String code, String message, String details) {
        return ApiError.builder()
                .code(code)
                .message(message)
                .details(details)
                .build();
    }
}

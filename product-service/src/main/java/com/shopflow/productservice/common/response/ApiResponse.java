package com.shopflow.productservice.common.response;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponse<T> {

    private int statusCode;
    private String message;
    private T data;

    // ─── Static Factory Methods ──────────────────

    // Success with data
    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(200, message, data);
    }

    // Created with data
    public static <T> ApiResponse<T> created(String message, T data) {
        return new ApiResponse<>(201, message, data);
    }

    // Success without data
    public static <T> ApiResponse<T> success(String message) {
        return new ApiResponse<>(200, message, null);
    }
}
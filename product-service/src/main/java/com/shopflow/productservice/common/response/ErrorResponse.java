package com.shopflow.productservice.common.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ErrorResponse {
    private int statusCode;
    private String message;
    private String error;
    private String path;
    private String timestamp;

    public ErrorResponse(int statusCode, String message,
                         String error, String path) {
        this.statusCode = statusCode;
        this.message = message;
        this.error = error;
        this.path = path;
        this.timestamp = LocalDateTime.now().toString();
    }
}
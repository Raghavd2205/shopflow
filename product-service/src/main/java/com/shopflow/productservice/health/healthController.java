package com.shopflow.productservice.health;

import com.shopflow.productservice.common.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/health")
public class healthController {
    @GetMapping
    public ResponseEntity<ApiResponse<Map<String, Object>>> getHealth() {
        return ResponseEntity.ok(
                ApiResponse.success(
                        "Data Fetched Successfully!!",
                        Map.of(
                                "status", "UP",
                                "service", "Product Service",
                                "timestamp", LocalDateTime.now().toString()
                        )
                )
        );
    }
}

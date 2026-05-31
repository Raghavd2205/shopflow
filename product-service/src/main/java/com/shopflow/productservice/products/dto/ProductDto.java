package com.shopflow.productservice.products.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ProductDto {

    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private Integer stockQuantity;
    private Boolean isActive;
    private CategorySummary category;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Nested DTO — avoid circular reference
    @Data
    public static class CategorySummary {
        private Long id;
        private String name;
    }
}
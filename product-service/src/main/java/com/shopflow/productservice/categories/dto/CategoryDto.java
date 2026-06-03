package com.shopflow.productservice.categories.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CategoryDto {

    private Long id;
    private String name;
    private String description;
    private Integer totalProducts;
    private LocalDateTime createdAt;
}
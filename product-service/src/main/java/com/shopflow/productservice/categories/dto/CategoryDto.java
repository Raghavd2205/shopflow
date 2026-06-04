package com.shopflow.productservice.categories.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize
public class CategoryDto {

    private Long id;
    private String name;
    private String description;
    private Integer totalProducts;
    private LocalDateTime createdAt;
}
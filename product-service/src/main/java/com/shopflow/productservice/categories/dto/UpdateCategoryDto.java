package com.shopflow.productservice.categories.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateCategoryDto {

    @Size(max = 100, message = "Name must not exceed 100 characters")
    private String name;

    private String description;
}
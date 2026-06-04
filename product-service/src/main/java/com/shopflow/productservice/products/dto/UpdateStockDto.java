package com.shopflow.productservice.products.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateStockDto {
    @NotNull(message="Stock value cannot be null")
    @Min(value=0,message="Stock cannot be negative")
    private Integer stockQuantity;
}

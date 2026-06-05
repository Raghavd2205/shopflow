package com.shopflow.orderService.orders.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class PlaceOrderDto {
    @NotNull(message = "Shipping Address is required")
    private String shippingAddress;


    @NotEmpty(message = "Order must have at least one item")
    @Valid  // ← validates each item in the list
    private List<OrderItemRequestDto> items;
}

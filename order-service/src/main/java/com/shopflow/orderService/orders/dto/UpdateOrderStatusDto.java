package com.shopflow.orderService.orders.dto;

import com.shopflow.orderService.orders.enums.OrderStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateOrderStatusDto {
    @NotNull(message = "Order status is required")
    private OrderStatus orderStatus;
}

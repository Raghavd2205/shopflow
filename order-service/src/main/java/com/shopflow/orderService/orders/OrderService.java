package com.shopflow.orderService.orders;

import com.shopflow.orderService.orders.dto.OrderDto;
import com.shopflow.orderService.orders.dto.PlaceOrderDto;

public interface OrderService {
    public OrderDto placeOrder(PlaceOrderDto payload, Long userId);
}

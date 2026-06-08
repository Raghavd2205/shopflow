package com.shopflow.orderService.orders;

import com.shopflow.orderService.orders.dto.OrderDto;
import com.shopflow.orderService.orders.dto.PlaceOrderDto;
import com.shopflow.orderService.orders.enums.OrderStatus;

import java.util.List;

public interface OrderService {
    public OrderDto placeOrder(PlaceOrderDto payload, Long userId);
    public List<OrderDto> listAllOrders(Long userId);
    public OrderDto listOrdersByOrderId(Long userId,Long orderId);
    public String cancelOrder(Long userId,Long orderId);
    public List<OrderDto> listAllOrdersForAdmin();
    public OrderDto updateOrderStatus(Long orderId, OrderStatus newStatus);
}

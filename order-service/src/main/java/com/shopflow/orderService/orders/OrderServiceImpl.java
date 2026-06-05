package com.shopflow.orderService.orders;

import com.shopflow.orderService.orders.dto.OrderDto;
import com.shopflow.orderService.orders.dto.PlaceOrderDto;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import com.shopflow.orderservice.clients.ProductClient;

@Service
@AllArgsConstructor
public class OrderServiceImpl implements OrderService{
    private final OrderRepository orderRepository;
    private final ProductClient productClient;
    @Override
    public OrderDto placeOrder(PlaceOrderDto payload, Long userId) {


        return null;
    }
}

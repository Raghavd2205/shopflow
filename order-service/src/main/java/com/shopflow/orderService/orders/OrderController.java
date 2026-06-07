package com.shopflow.orderService.orders;

import com.shopflow.orderService.common.response.ApiResponse;
import com.shopflow.orderService.orders.dto.OrderDto;
import com.shopflow.orderService.orders.dto.PlaceOrderDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/order")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;
    @PostMapping
    public ResponseEntity<ApiResponse<OrderDto>> placeOrder(@RequestBody PlaceOrderDto orderPayload, Authentication authentication){
        // Get userId from JWT — set by JwtAuthFilter
        Long userId = (Long) authentication.getDetails();
        System.out.println("authentication.getDetails()"+authentication.getDetails());

        OrderDto order = orderService.placeOrder(orderPayload, userId);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.created("Order placed successfully", order));

    }
}

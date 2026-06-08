package com.shopflow.orderService.orders;

import com.shopflow.orderService.common.response.ApiResponse;
import com.shopflow.orderService.orders.dto.OrderDto;
import com.shopflow.orderService.orders.dto.PlaceOrderDto;
import com.shopflow.orderService.orders.dto.UpdateOrderStatusDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/order")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;
    @PostMapping
    public ResponseEntity<ApiResponse<OrderDto>> placeOrder(@RequestBody @Valid PlaceOrderDto orderPayload, Authentication authentication){
        // Get userId from JWT — set by JwtAuthFilter
        Long userId = (Long) authentication.getDetails();
        System.out.println("authentication.getDetails()"+authentication.getDetails());

        OrderDto order = orderService.placeOrder(orderPayload, userId);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.created("Order placed successfully", order));

    }
    @GetMapping
    public ResponseEntity<ApiResponse<List<OrderDto>>> listAllOrders(Authentication authentication){
        Long userId = (Long) authentication.getDetails();
        System.out.println("authentication"+authentication);
        return ResponseEntity.ok(ApiResponse.success("Orders List Fetched Successfully",orderService.listAllOrders(userId)));
    }
    @GetMapping("/{orderId}")
    public ResponseEntity<ApiResponse<OrderDto>> listOrderById(Authentication authentication,@PathVariable Long orderId){
        Long userId = (Long) authentication.getDetails();
        return ResponseEntity.ok(ApiResponse.success("Order Fetched Successfully",orderService.listOrdersByOrderId(userId,orderId)));
    }
    @PatchMapping("/{orderId}")
    public ResponseEntity<ApiResponse<String>> cancelOrderById(Authentication authentication,@PathVariable Long orderId){
        Long userId = (Long) authentication.getDetails();
        return ResponseEntity.ok(ApiResponse.delete(orderService.cancelOrder(userId,orderId)));
    }
    @GetMapping("/admin")
    public ResponseEntity<ApiResponse<List<OrderDto>>> listAllOrdersForAdmin(Authentication authentication){
        System.out.println("authentication"+authentication);
        return ResponseEntity.ok(ApiResponse.success("Orders List Fetched Successfully",orderService.listAllOrdersForAdmin()));
    }
    @PutMapping("/admin/{id}/status")
    public ResponseEntity<ApiResponse<OrderDto>> updateOrderStatus(
            @PathVariable Long id,
            @RequestBody @Valid UpdateOrderStatusDto dto
    ) {
        OrderDto order = orderService.updateOrderStatus(id, dto.getOrderStatus());
        return ResponseEntity.ok(
                ApiResponse.success("Order status updated successfully", order)
        );
    }
}

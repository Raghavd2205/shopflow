package com.shopflow.orderService.orders;

import com.shopflow.orderService.clients.dto.ProductDto;
import com.shopflow.orderService.clients.dto.UpdateStockDto;
import com.shopflow.orderService.common.exception.BadRequestException;
import com.shopflow.orderService.common.exception.ResourceNotFoundException;
import com.shopflow.orderService.orders.dto.OrderDto;
import com.shopflow.orderService.orders.dto.OrderItemDto;
import com.shopflow.orderService.orders.dto.PlaceOrderDto;
import com.shopflow.orderService.orders.enums.OrderStatus;
import jakarta.persistence.criteria.Order;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import com.shopflow.orderService.clients.ProductClient;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class OrderServiceImpl implements OrderService{
    private final OrderRepository orderRepository;
    private final ProductClient productClient;
    @Override
    @Transactional
    public OrderDto placeOrder(@Valid PlaceOrderDto payload, Long userId) {
        List<OrderItem> orderItems =   payload.getItems().stream().map(itemRequest ->{
            ProductDto product = productClient.getProductById(itemRequest.getProductId()).getData();
            System.out.println("Product"+product);
            if(!product.getIsActive()){
                throw new BadRequestException("Product " + product.getName() + " is not available");
            }
            if(product.getStockQuantity()<itemRequest.getQuantity()){
                throw new BadRequestException(
                        "Insufficient stock for " + product.getName() +
                                ". Available: " + product.getStockQuantity() +
                                ", Requested: " + itemRequest.getQuantity()
                );
            }
            BigDecimal subtotal = product.getPrice()
                    .multiply(BigDecimal.valueOf(itemRequest.getQuantity()));
            return OrderItem.builder()
                    .productId(product.getId())
                    .productName(product.getName())
                    .subtotal(subtotal)
                    .unitPrice(product.getPrice())
                    .quantity(itemRequest.getQuantity())
                    .build();
        }).toList();
        BigDecimal totalAmount = orderItems.stream().map(OrderItem::getSubtotal)
                                  .reduce(BigDecimal.ZERO,BigDecimal::add);
        Orders order = Orders.builder()
                .userId(userId)
                .totalAmount(totalAmount)
                .status(OrderStatus.PENDING)
                .shippingAddress(payload.getShippingAddress())
                .items(orderItems)
                .build();

        // Link each item to the order
        orderItems.forEach(item->item.setOrder(order));

        Orders savedOrder = this.orderRepository.save(order);

        //update stock
        orderItems.forEach(item->{
            ProductDto product = productClient.getProductById(item.getProductId()).getData();
            Integer stockQuantity = product.getStockQuantity() -item.getQuantity();
            UpdateStockDto updateStock = UpdateStockDto.builder()
                    .stockQuantity(stockQuantity)
                    .build();
            ProductDto productDto1 = productClient.updateStock(product.getId(),updateStock).getData();
        });


        return toOrderDto(savedOrder);
    }

    @Override
    public List<OrderDto> listAllOrders(Long userId) {
        List<Orders> orders = this.orderRepository.findByUserIdOrderByCreatedAtDesc(userId);
        if(orders.isEmpty()){
            throw new ResourceNotFoundException("No Orders found !!");
        }
        return orders.stream().map(this::toOrderDto).toList();
    }

    @Override
    public OrderDto listOrdersByOrderId(Long userId, Long orderId) {
        Orders order = this.orderRepository.findByOrderIdandUserId(userId,orderId);
        if(order == null){
            throw new ResourceNotFoundException("No Order with Id "+orderId+" is found for the user");
        }
        return toOrderDto(order);
    }

    @Override
    @Transactional
    public String cancelOrder(Long userId, Long orderId) {
        Orders order = this.orderRepository.findByOrderIdandUserId(userId,orderId);
        if(order == null){
            throw new ResourceNotFoundException("No Order with Id "+orderId+" is found for the user");
        }
        if(order.getStatus() != OrderStatus.PENDING){
            throw new BadRequestException("Only Pending order can be cancelled");
        }
        order.setStatus(OrderStatus.CANCELLED);
        Orders savedResponse = this.orderRepository.save(order);
        if(savedResponse.getStatus() != OrderStatus.CANCELLED){
            throw new RuntimeException("Something went wrong !!");
        }
        savedResponse.getItems().forEach(item->{
            ProductDto product = productClient.getProductById(item.getProductId()).getData();
            Integer stockQuantity = product.getStockQuantity() +item.getQuantity();
            UpdateStockDto updateStock = UpdateStockDto.builder()
                    .stockQuantity(stockQuantity)
                    .build();
            ProductDto productDto1 = productClient.updateStock(product.getId(),updateStock).getData();
        });
        return "Order Cancelled successfully";
    }

    @Override
    public List<OrderDto> listAllOrdersForAdmin() {
        return this.orderRepository.findAll().stream().map(this::toOrderDto).toList();
    }

    @Override
    public OrderDto updateOrderStatus(Long orderId, OrderStatus newStatus) {
        Orders order = this.orderRepository.findById(orderId).orElseThrow(()->new ResourceNotFoundException("No Order Found with given Order Id"));
        if(order.getStatus() == OrderStatus.CANCELLED || order.getStatus() == OrderStatus.DELIVERED){
            throw new BadRequestException(
                    "Cannot update status of " + order.getStatus() + " order"
            );
        }
        order.setStatus(newStatus);
        return toOrderDto(orderRepository.save(order));
    }

    private OrderDto toOrderDto(Orders order){
        OrderDto dto = new OrderDto();
        dto.setId(order.getId());
        dto.setStatus(order.getStatus());
        dto.setTotalAmount(order.getTotalAmount());
        dto.setCreatedAt(order.getCreatedAt());
        dto.setUpdatedAt(order.getUpdatedAt());
        dto.setUserId(order.getUserId());
        dto.setShippingAddress(order.getShippingAddress());

        if (order.getItems() != null) {
            List<OrderItemDto> itemDtos = order.getItems().stream()
                    .map(item -> {
                        OrderItemDto itemDto = new OrderItemDto();
                        itemDto.setId(item.getId());
                        itemDto.setProductId(item.getProductId());
                        itemDto.setProductName(item.getProductName());
                        itemDto.setQuantity(item.getQuantity());
                        itemDto.setUnitPrice(item.getUnitPrice());
                        itemDto.setSubtotal(item.getSubtotal());
                        return itemDto;
                    })
                    .collect(Collectors.toList());
            dto.setItems(itemDtos);
        }
        return dto;
    }
}

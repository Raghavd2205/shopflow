package com.shopflow.orderService.orders;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Entity
@Table(name = "order_items")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Many items belong to one order
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Orders order;

    // Product ID from Product Service — no Product entity needed
    @Column(nullable = false)
    private Long productId;

    // Store product name at time of order
    // Product name might change later — we preserve order history
    @Column(nullable = false)
    private String productName;

    @Column(nullable = false)
    @Min(value = 1,message = "Quantity should be greater then 0")
    private Integer quantity;

    // Store price at time of order
    // Price might change later — we preserve what customer paid
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal unitPrice;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal subtotal;
}
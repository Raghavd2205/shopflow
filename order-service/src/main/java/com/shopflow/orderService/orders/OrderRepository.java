package com.shopflow.orderService.orders;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderRepository extends JpaRepository<Orders,Long> {
    @Query("SELECT o FROM Orders o "+
            "JOIN FETCH o.items "+
            "WHERE o.userId = :userId "+
            "ORDER BY o.createdAt DESC")
    List<Orders> findByUserIdOrderByCreatedAtDesc(@Param("userId") long userId);

    @Query("SELECT o FROM Orders o "+
            "JOIN FETCH o.items "+
            "WHERE o.userId = :userId AND o.id = :orderId")
    Orders findByOrderIdandUserId(@Param("userId") long userId,@Param("orderId") long orderId);
}

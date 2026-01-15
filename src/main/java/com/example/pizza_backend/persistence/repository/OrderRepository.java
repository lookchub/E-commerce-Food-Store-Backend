package com.example.pizza_backend.persistence.repository;

import com.example.pizza_backend.persistence.entity.Orders;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Orders,Long> {
    List<Orders> getOrdersByProfileProfileId(Long profileId);

    @Query("""
        SELECT o
        FROM Orders o
        WHERE 1=1
          AND (:orderId IS NULL OR o.orderId = :orderId)
          AND (:status IS NULL OR o.status = :status)
    """)
    List<Orders> searchOrder(@Param("orderId") Long orderId,  @Param("status") Integer status);
}

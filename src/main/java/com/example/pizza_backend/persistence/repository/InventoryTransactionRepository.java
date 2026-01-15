package com.example.pizza_backend.persistence.repository;

import com.example.pizza_backend.persistence.entity.InventoryTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface InventoryTransactionRepository extends JpaRepository<InventoryTransaction ,Long> {

    @Query("SELECT SUM(it.qtyChange) FROM InventoryTransaction it WHERE it.product.productId = :productId " +
            "AND it.transactionType = :type " +
            "AND (it.createdAt >= :startDate) " +
            "AND (it.createdAt <= :endDate)")
    Integer sumQtyChangeByProductIdAndType(
            @Param("productId") Long productId,
            @Param("type") Integer type,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    @Query("SELECT it FROM InventoryTransaction it WHERE it.product.productId = :productId " +
            "AND it.transactionType = :type " +
            "AND (it.createdAt >= :startDate) " +
            "AND (it.createdAt <= :endDate)")
    InventoryTransaction findByProductIdAndType(
            @Param("productId") Long productId,
            @Param("type") Integer type,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

}

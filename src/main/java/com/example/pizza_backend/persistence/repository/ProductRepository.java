package com.example.pizza_backend.persistence.repository;


import com.example.pizza_backend.persistence.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query(value = """
        SELECT * FROM product p
        WHERE 1=1
          AND (:productId IS NULL OR p.product_id = :productId)
          AND (
            :productName IS NULL OR 
            LOWER(REPLACE(CAST(p.product_name AS text), ' ', '')) 
              LIKE LOWER(CONCAT('%', REPLACE(:productName, ' ', ''), '%'))
          )
          AND (:productPrice IS NULL OR p.product_price = :productPrice)
          AND (:productStock IS NULL OR p.product_stock = :productStock)
          AND (:isActive IS NULL OR p.is_active = :isActive)
          AND (:categoryId IS NULL OR p.category_id = :categoryId)
        ORDER BY p.product_id
    """, nativeQuery = true)
    List<Product> searchProducts(
            @Param("productId") Long  productId,
            @Param("productName") String productName,
            @Param("productPrice") Integer productPrice,
            @Param("productStock") Integer productStock,
            @Param("categoryId" ) Long categoryId,
            @Param("isActive") Integer isActive
    );



}

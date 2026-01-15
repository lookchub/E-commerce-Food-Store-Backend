package com.example.pizza_backend.persistence.repository;


import com.example.pizza_backend.persistence.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category,Long> {

    @Query("SELECT c FROM Category c WHERE 1=1" +
            "AND (:categoryId IS NULL OR c.categoryId = :categoryId)" +
            "AND (:categoryName IS NULL OR c.categoryName LIKE %:categoryName%)")
    List<Category> searchCategory(
            @Param("categoryId") Long categoryId,
            @Param("categoryName") String categoryName
    );
}

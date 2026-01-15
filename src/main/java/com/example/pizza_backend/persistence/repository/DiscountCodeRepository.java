package com.example.pizza_backend.persistence.repository;

import com.example.pizza_backend.persistence.entity.DiscountCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DiscountCodeRepository extends JpaRepository<DiscountCode,Long> {
    Optional<DiscountCode> findByCode(String code);
}

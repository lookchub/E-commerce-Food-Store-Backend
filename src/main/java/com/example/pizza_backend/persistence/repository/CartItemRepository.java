package com.example.pizza_backend.persistence.repository;

import com.example.pizza_backend.persistence.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem,Long> {
    List<CartItem> findByCartCartId(Long cartId);

    @Query("""
        SELECT ci.cartItemId
        FROM CartItem ci
        WHERE ci.cart.cartId = (
            SELECT c.cartId
            FROM Cart c
            WHERE c.profile.profileId = :profileId
        )
    """)
    List<Long> findCartItemIdsByProfileId(@Param("profileId") Long profileId);

    Optional<CartItem> findByCartCartIdAndProductProductId(Long cartCartId, Long productId);

    void deleteByCartCartId(Long cartCartId);
}

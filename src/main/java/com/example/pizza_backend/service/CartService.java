package com.example.pizza_backend.service;

import com.example.pizza_backend.api.dto.response.CartResponse;
import com.example.pizza_backend.exception.IdNotFoundException;
import com.example.pizza_backend.mapper.CartMapper;
import com.example.pizza_backend.persistence.entity.Cart;
import com.example.pizza_backend.persistence.repository.CartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CartService{
    private CartRepository cartRepository;
    private CartMapper cartMapper;

    @Autowired
    public CartService(CartRepository cartRepository, CartMapper cartMapper) {
        this.cartRepository = cartRepository;
        this.cartMapper = cartMapper;
    }

    public CartResponse getCartDtoByProfileId(Long profileId) {
        if (profileId == null) {
            throw new IllegalArgumentException("profileId cannot be null");
        }
        Cart cart = cartRepository.findByProfileProfileId(profileId)
                .orElseThrow(() -> new IdNotFoundException("Cart not found for this user"));
        return cartMapper.toCartResponse(cart);
    }

    public Cart getCartByProfileId(Long profileId) {
        return cartRepository.findByProfileProfileId(profileId)
                .orElseThrow(() -> new IdNotFoundException("Cart not found for this user"));
    }
}

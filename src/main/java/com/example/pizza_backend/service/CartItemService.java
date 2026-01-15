package com.example.pizza_backend.service;

import com.example.pizza_backend.api.dto.request.CartItemRequest;
import com.example.pizza_backend.api.dto.response.CartItemResponse;
import com.example.pizza_backend.exception.AccessDeniedException;
import com.example.pizza_backend.exception.IdNotFoundException;
import com.example.pizza_backend.mapper.CartItemMapper;
import com.example.pizza_backend.persistence.entity.Cart;
import com.example.pizza_backend.persistence.entity.CartItem;
import com.example.pizza_backend.persistence.entity.Product;
import com.example.pizza_backend.persistence.repository.CartItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class CartItemService{
    private final CartItemRepository cartItemRepository;
    private final CartItemMapper cartItemMapper;
    private final CartService cartService;
    private final ProductService productService;

    @Autowired
    public CartItemService(CartItemRepository cartItemRepository,
                           CartItemMapper cartItemMapper,
                           CartService cartService,
                           ProductService productService) {
        this.cartItemRepository = cartItemRepository;
        this.cartItemMapper = cartItemMapper;
        this.cartService = cartService;
        this.productService = productService;
    }

    public boolean checkConstrain(Integer qty, Integer lineTotal){
        if (lineTotal < 0 || qty < 0){
            return false;
        }
        return true;
    }
    
    public List<CartItemResponse> getCartItemsByCartId(Long cartId) {
        if (cartId == null) {
            throw new IllegalArgumentException("cartId cannot be null");
        }
        List<CartItem> cartItems = cartItemRepository.findByCartCartId(cartId);
        return cartItems.stream()
                .map(c->cartItemMapper.toCartItemResponse(c))
                .toList();
    }


    @Transactional
    public CartItemResponse createCartItem(CartItemRequest cartItemRequest, Long profileId) {
        Cart cart = cartService.getCartByProfileId(profileId);

        if (cartItemRequest.getProductId() == null) {
            throw new IllegalArgumentException("The given product Id cannot be null");
        }

        Product product = productService.getProductById(cartItemRequest.getProductId());

        //find product already in the cart?
        Optional<CartItem> existingItemOpt = cartItemRepository.findByCartCartIdAndProductProductId(cart.getCartId(), product.getProductId());

        if (existingItemOpt.isPresent()) {
            throw new IllegalArgumentException(
                    "Product already in cart. Use update endpoint instead."
            );
        }

        CartItem cartItem = cartItemMapper.toCartItem(cartItemRequest);
        if (!checkConstrain(cartItem.getQty(), cartItem.getLineTotal())) {
            throw new IllegalArgumentException("qty must be > 0 and lineTotal must be > 0");
        }

        cartItem.setCart(cart);
        cartItem.setProduct(product);
        return cartItemMapper.toCartItemResponse(cartItemRepository.save(cartItem));

    }


    @Transactional
    public CartItemResponse updateCartItem(CartItemRequest cartItemRequest, Long profileId) {

        if (cartItemRequest.getCartItemId() == null) {
            throw new IllegalArgumentException("The given cart item Id cannot be null");
        }
        CartItem cartItem = cartItemRepository.findById(cartItemRequest.getCartItemId())
                .orElseThrow(() -> new IdNotFoundException("Cart Item Not found"));
        if (!cartItem.getCart().getProfile().getProfileId().equals(profileId)) {
            throw new AccessDeniedException("This cart does not belong to current user");
        }
        if (!checkConstrain(cartItem.getQty(), cartItem.getLineTotal())) {
            throw new IllegalArgumentException("qty must be > 0 and lineTotal must be > 0");
        }
        cartItemMapper.updateCartItemFromInput(cartItemRequest, cartItem);
        CartItem updatedCartItem = cartItemRepository.save(cartItem);

        return cartItemMapper.toCartItemResponse(updatedCartItem);
    }


    @Transactional
    public void deleteCartItem(CartItemRequest cartItemRequest, Long profileId) {

        if (cartItemRequest.getCartItemId() == null) {
            throw new IllegalArgumentException("The given cart item Id cannot be null");
        }
        CartItem cartItem = cartItemRepository.findById(cartItemRequest.getCartItemId())
                .orElseThrow(() -> new IdNotFoundException("Cart Item Not found"));
        if (!cartItem.getCart().getProfile().getProfileId().equals(profileId)) {
            throw new AccessDeniedException("This cart item does not belong to current user");
        }
        cartItemRepository.deleteById(cartItemRequest.getCartItemId());

    }


    @Transactional
    public void clearAllCartItem(Long profileId) {
        Cart cart = cartService.getCartByProfileId(profileId);
        Long cartId = cart.getCartId();
        cartItemRepository.deleteByCartCartId(cartId);
    }
}

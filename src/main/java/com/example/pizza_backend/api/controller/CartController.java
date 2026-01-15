package com.example.pizza_backend.api.controller;

import com.example.pizza_backend.api.dto.request.CartItemRequest;
import com.example.pizza_backend.api.dto.response.CartItemResponse;
import com.example.pizza_backend.api.dto.response.CartResponse;
import com.example.pizza_backend.service.CartItemService;
import com.example.pizza_backend.service.CartService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/cart")
public class CartController {

    private final CartService cartService;
    private final CartItemService cartItemService;
    @Autowired
    public CartController(CartService cartService, CartItemService cartItemService) {
        this.cartService = cartService;
        this.cartItemService = cartItemService;
    }


    @GetMapping("/list")
    public ResponseEntity<?> getCart(HttpServletRequest request) {
        Long profileId = (Long) request.getAttribute("profile_id");

        CartResponse cart = cartService.getCartDtoByProfileId(profileId);
        List<CartItemResponse> cartItems = cartItemService.getCartItemsByCartId(cart.getCartId());

        //map cart,cartItem
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("cart", cart);
        response.put("cartItems", cartItems);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/addItems")
    public ResponseEntity<?> addItem(HttpServletRequest request, @RequestBody CartItemRequest cartItemRequest) {
        Long profileId = (Long) request.getAttribute("profile_id");
        cartItemService.createCartItem(cartItemRequest, profileId);
        return ResponseEntity.ok().body(Map.of("message", "Add items successfully"));
    }

    @PostMapping("/updateItems")
    public ResponseEntity<?> updateItem(HttpServletRequest request, @RequestBody CartItemRequest cartItemRequest) {
        Long profileId = (Long) request.getAttribute("profile_id");
        cartItemService.updateCartItem(cartItemRequest, profileId);
        return ResponseEntity.ok().body(Map.of("message", "Update items successfully"));
    }

    @PostMapping("/deleteItems")
    public ResponseEntity<?> deleteItem(HttpServletRequest request, @RequestBody CartItemRequest cartItemRequest) {
        Long profileId = (Long) request.getAttribute("profile_id");
        cartItemService.deleteCartItem(cartItemRequest, profileId);
        return ResponseEntity.ok().body(Map.of("message", "Delete items successfully"));
    }

}

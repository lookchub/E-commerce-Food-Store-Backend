package com.example.pizza_backend.api.dto.request.order;

import com.example.pizza_backend.api.dto.request.CartItemRequest;
import com.example.pizza_backend.api.dto.response.DiscountCodeResponse;
import lombok.Data;

import java.util.List;

@Data
public class OrderAndItemRequest {
    private OrderRequest orderInput;
    private List<CartItemRequest> cartItemInputs;
    private DiscountCodeResponse discountCode;
}

package com.example.pizza_backend.api.dto.request.order;

import lombok.Data;

@Data
public class OrderRequest {
    private Long orderId;
    private Integer status;
    private Integer subtotal;
    private Integer deliveryFee;
    private Integer grandTotal;
}

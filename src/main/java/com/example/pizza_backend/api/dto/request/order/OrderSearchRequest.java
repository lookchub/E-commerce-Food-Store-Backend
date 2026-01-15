package com.example.pizza_backend.api.dto.request.order;

import lombok.Data;

@Data
public class OrderSearchRequest {
    private Long orderId;
    private Integer status;
}

package com.example.pizza_backend.api.dto.request;

import lombok.Data;

@Data
public class PromotionProductRequest {
    private Long recommendedId;
    private Long productId;
}

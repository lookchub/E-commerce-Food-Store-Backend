package com.example.pizza_backend.api.dto.request.product;

import lombok.Data;

@Data
public class ProductRequest {
    private Long productId;
    private String productName;
    private String productDetail;
    private Integer productPrice;
    private Integer productStock;
    private Integer stockType;
    private Integer isActive;
    private Long categoryId;
}

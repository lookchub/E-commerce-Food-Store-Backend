package com.example.pizza_backend.api.dto.request.product;

import lombok.Data;

@Data
public class ProductSearchRequest {
    private Long  productId;
    private String productName;
    private Integer productPrice;
    private Integer productStock;
    private Long categoryId;
    private Integer isActive;
}

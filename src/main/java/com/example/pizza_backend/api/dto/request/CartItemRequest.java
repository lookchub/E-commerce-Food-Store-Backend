package com.example.pizza_backend.api.dto.request;

import lombok.Data;

@Data
public class CartItemRequest {
    private Long cartItemId;
    private Long productId;
    private Integer qty;
    private Integer lineTotal;

    //สำหรับเพิ่มใส่ order
    private String productName;
    private String productDetail;
    private Double productPrice;
}

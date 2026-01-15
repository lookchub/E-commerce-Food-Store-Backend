package com.example.pizza_backend.api.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CartItemResponse {
    private Long cartItemId;
    private Long productId;
    private String productName;
    private String productDetail;
    private Integer productPrice;
    private String productImgPath;
    private Integer qty;
    private Integer lineTotal;
}

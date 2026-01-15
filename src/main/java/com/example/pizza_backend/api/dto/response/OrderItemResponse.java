package com.example.pizza_backend.api.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderItemResponse {
    private Long orderItemId;
    private Long productIdSnapshot;
    private String productName;
    private String productDetail;
    private Double productPrice;
    private Integer qty;
    private Integer lineTotal;
}

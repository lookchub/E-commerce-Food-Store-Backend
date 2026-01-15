package com.example.pizza_backend.api.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class InventoryResponse {
    private Long productId;
    private String productName;
    private String categoryName;
    private Integer price;
    private Integer stock;
    private Integer sold;
}

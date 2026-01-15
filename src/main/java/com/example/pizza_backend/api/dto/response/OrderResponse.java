package com.example.pizza_backend.api.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderResponse {
    private Long orderId;
    private String username;
    private Integer status;
    private Integer subtotal;
    private Integer deliveryFee;
    private Integer grandTotal;
    private LocalDate createdAt;
}

package com.example.pizza_backend.api.dto.request.order;

import com.example.pizza_backend.api.dto.response.OrderItemResponse;
import lombok.Data;

import java.util.List;

@Data
public class OrderItemsRequest {
    private List<OrderItemResponse> orderItems;
}

package com.example.pizza_backend.service;

import com.example.pizza_backend.api.dto.response.OrderItemResponse;
import com.example.pizza_backend.mapper.OrderItemMapper;
import com.example.pizza_backend.persistence.entity.OrderItem;
import com.example.pizza_backend.persistence.repository.OrderItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderItemService {
    private final OrderItemRepository orderItemRepository;
    private final OrderItemMapper orderItemMapper;

    @Autowired
    public OrderItemService(OrderItemRepository orderItemRepository, OrderItemMapper orderItemMapper) {
        this.orderItemRepository = orderItemRepository;
        this.orderItemMapper = orderItemMapper;
    }


    public List<OrderItemResponse> getOrderItems(Long orderId) {
        List<OrderItem> orderItems = orderItemRepository.findByOrderOrderId(orderId);
        return orderItems.stream()
                .map(item -> orderItemMapper.toOrderItemResponse(item))
                .toList();
    }
}

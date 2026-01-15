package com.example.pizza_backend.mapper;

import com.example.pizza_backend.api.dto.request.CartItemRequest;
import com.example.pizza_backend.api.dto.response.OrderItemResponse;
import com.example.pizza_backend.persistence.entity.OrderItem;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@org.mapstruct.Mapper(componentModel = "spring")
public interface OrderItemMapper {
    OrderItemMapper INSTANCE = Mappers.getMapper(OrderItemMapper.class);

    //CREATE
    @Mapping(source = "productId", target = "productIdSnapshot")
    @Mapping(target = "order", ignore = true)
    OrderItem toOrderItem(CartItemRequest cartItemInput);

    //READ
    OrderItemResponse toOrderItemResponse(OrderItem orderItem);
}

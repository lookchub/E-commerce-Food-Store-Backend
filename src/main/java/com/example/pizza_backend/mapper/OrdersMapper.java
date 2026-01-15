package com.example.pizza_backend.mapper;

import com.example.pizza_backend.api.dto.request.order.OrderRequest;
import com.example.pizza_backend.api.dto.response.OrderResponse;
import com.example.pizza_backend.persistence.entity.Orders;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

@org.mapstruct.Mapper(componentModel = "spring")
public interface OrdersMapper {
    OrdersMapper INSTANCE = Mappers.getMapper(OrdersMapper.class);

    //CREATE
    @Mapping(target = "orderId", ignore = true)
    @Mapping(target = "profile", ignore = true)
    @Mapping(target = "address", ignore = true)
    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    Orders toOrder(OrderRequest orderInput);

    //UPDATE
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "orderId", ignore = true)
    @Mapping(target = "profile", ignore = true)
    @Mapping(target = "address", ignore = true)
    @Mapping(target = "fulfilledAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "fulfilledBy", expression = "java(name)")
    void updateOrderFromInput(OrderRequest orderInput, @MappingTarget Orders orders, @Context String name);

    //READ
    @Mapping(source = "profile.username", target = "username")
    OrderResponse toOrderResponse(Orders order);
}

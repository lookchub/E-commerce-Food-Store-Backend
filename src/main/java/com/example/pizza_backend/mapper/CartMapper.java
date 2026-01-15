package com.example.pizza_backend.mapper;

import com.example.pizza_backend.api.dto.response.CartResponse;
import com.example.pizza_backend.persistence.entity.Cart;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@org.mapstruct.Mapper(componentModel = "spring")
public interface CartMapper {
    CartMapper INSTANCE = Mappers.getMapper(CartMapper.class);

    //READ
    @Mapping(target = "cartItems", ignore = true)
    CartResponse toCartResponse(Cart cart);


}

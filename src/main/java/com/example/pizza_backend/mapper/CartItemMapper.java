package com.example.pizza_backend.mapper;

import com.example.pizza_backend.api.dto.request.CartItemRequest;
import com.example.pizza_backend.api.dto.response.CartItemResponse;
import com.example.pizza_backend.persistence.entity.CartItem;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

@org.mapstruct.Mapper(componentModel = "spring")
public interface CartItemMapper {
    CartItemMapper INSTANCE = Mappers.getMapper(CartItemMapper.class);

    //CREATE
    @Mapping(target = "cart", ignore = true)
    @Mapping(target = "product", ignore = true)
    @Mapping(target = "cartItemId", ignore = true)
    CartItem toCartItem(CartItemRequest cartItemInput);

    //UPDATE
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "cartItemId", ignore = true)
    @Mapping(target = "product", ignore = true)
    @Mapping(target = "cart", ignore = true)
    void updateCartItemFromInput(CartItemRequest cartItemInput, @MappingTarget CartItem cartItem);

    //READ
    @Mapping(source = "product.productId", target = "productId")
    @Mapping(source = "product.productName", target = "productName")
    @Mapping(source = "product.productDetail", target = "productDetail")
    @Mapping(source = "product.productPrice", target = "productPrice")
    @Mapping(source = "product.productImgPath", target = "productImgPath")
    CartItemResponse toCartItemResponse(CartItem cartItem);
}
